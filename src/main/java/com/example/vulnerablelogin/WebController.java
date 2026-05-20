package com.example.vulnerablelogin;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebController {
    private final AccountRepository accountRepository;

    public WebController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String home() {
        return page("Vulnerable Login Lab", """
                <p>Use this local lab to test intentionally insecure login, registration, account APIs, and delete-account flows.</p>
                <p><a href="/login">Login</a> | <a href="/register">Register</a> | <a href="/accounts">Accounts</a></p>
                """);
    }

    @GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String loginPage() {
        return page("Login", """
                <form method="post" action="/login">
                    <label>Username <input name="username" autocomplete="username"></label>
                    <label>Password <input name="password" type="password" autocomplete="current-password"></label>
                    <button type="submit">Login</button>
                </form>
                <p>Try seeded account <code>admin / admin123</code>.</p>
                """);
    }

    @PostMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        Account account = accountRepository.login(username, password);
        if (account == null) {
            return page("Login Failed", "<p>No matching account.</p><p><a href=\"/login\">Try again</a></p>");
        }

        return page("Login Success", """
                <p>Welcome, %s.</p>
                <p>Your role is <strong>%s</strong>.</p>
                <p><a href="/accounts">View accounts</a></p>
                """.formatted(account.username(), account.role()));
    }

    @GetMapping(value = "/register", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String registerPage() {
        return page("Register", """
                <form method="post" action="/register">
                    <label>Username <input name="username" autocomplete="username"></label>
                    <label>Password <input name="password" type="password" autocomplete="new-password"></label>
                    <label>Email <input name="email" type="email"></label>
                    <label>Role <input name="role" value="USER"></label>
                    <button type="submit">Create account</button>
                </form>
                """);
    }

    @PostMapping(value = "/register", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "email", required = false) String email,
                           @RequestParam(value = "role", required = false) String role) {
        accountRepository.create(new AccountRequest(username, password, email, role));
        return page("Registered", """
                <p>Created account for %s.</p>
                <p><a href="/login">Login</a> | <a href="/accounts">View accounts</a></p>
                """.formatted(username));
    }

    @GetMapping(value = "/accounts", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String accountsPage() {
        StringBuilder rows = new StringBuilder();
        for (Account account : accountRepository.findAll()) {
            rows.append("""
                    <tr>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>
                            <form method="post" action="/accounts/delete">
                                <input type="hidden" name="username" value="%s">
                                <button type="submit">Delete</button>
                            </form>
                        </td>
                    </tr>
                    """.formatted(
                    account.id(),
                    account.username(),
                    account.password(),
                    account.email(),
                    account.role(),
                    account.username()));
        }

        return page("Accounts", """
                <p>Total accounts: %d</p>
                <table>
                    <thead>
                        <tr><th>ID</th><th>Username</th><th>Password</th><th>Email</th><th>Role</th><th>Action</th></tr>
                    </thead>
                    <tbody>%s</tbody>
                </table>
                <p><a href="/register">Add account</a></p>
                """.formatted(accountRepository.count(), rows));
    }

    @PostMapping(value = "/accounts/delete", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String deleteAccount(@RequestParam("username") String username) {
        int deleted = accountRepository.deleteByUsername(username);
        return page("Delete Account", """
                <p>Deleted %d account(s) for username: %s</p>
                <p><a href="/accounts">Back to accounts</a></p>
                """.formatted(deleted, username));
    }

    private String page(String title, String body) {
        return """
                <!doctype html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>%s</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 2rem auto; max-width: 980px; line-height: 1.45; }
                        form { display: grid; gap: 0.75rem; max-width: 420px; }
                        input, button { font: inherit; padding: 0.5rem; }
                        table { border-collapse: collapse; width: 100%%; }
                        th, td { border: 1px solid #ccc; padding: 0.5rem; text-align: left; }
                    </style>
                </head>
                <body>
                    <h1>%s</h1>
                    %s
                </body>
                </html>
                """.formatted(title, title, body);
    }
}
