# UI Testing with Playwright

A comprehensive guide to automated UI testing using Playwright, a powerful cross-browser testing framework.

## Table of Contents

- [Introduction](#introduction)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Writing Tests](#writing-tests)
- [Selectors](#selectors)
- [Interactions](#interactions)
- [Assertions](#assertions)
- [Running Tests](#running-tests)
- [Debugging](#debugging)
- [Best Practices](#best-practices)
- [Advanced Topics](#advanced-topics)
- [Troubleshooting](#troubleshooting)

## Introduction

Playwright is a Node.js library for browser automation and testing. It supports:

- **Chromium**, **Firefox**, and **WebKit** browsers
- **Desktop and mobile** testing
- **Multiple languages**: JavaScript, Python, Java, .NET
- **Cross-platform**: Windows, macOS, Linux

### Key Features

- **Fast and reliable** with zero flakiness
- **Headless and headed modes** for debugging
- **Multiple pages and contexts** in a single test
- **Network mocking and request interception**
- **Screenshots and video recording**
- **Accessibility testing**

## Installation

### Prerequisites

- Node.js 16+ (for JavaScript/TypeScript)
- npm or yarn

### Setup

1. **Initialize a new project:**

```bash
npm init -y
npm install --save-dev @playwright/test
```

2. **Install browsers:**

```bash
npx playwright install
```

3. **Generate a test scaffold:**

```bash
npm init playwright@latest
```

This command creates a basic Playwright project structure with example tests and configuration.

## Quick Start

### Running Your First Test

Create a file `example.spec.js`:

```javascript
import { test, expect } from '@playwright/test';

test('homepage has title', async ({ page }) => {
  await page.goto('https://example.com');
  await expect(page).toHaveTitle(/Example/);
});
```

Run the test:

```bash
npx playwright test example.spec.js
```

## Writing Tests

### Test Structure

```javascript
import { test, expect } from '@playwright/test';

test('user can log in', async ({ page, context, browser }) => {
  // Setup
  await page.goto('https://example.com/login');

  // Action
  await page.fill('input[name="username"]', 'testuser');
  await page.fill('input[name="password"]', 'password123');
  await page.click('button[type="submit"]');

  // Assert
  await expect(page).toHaveURL(/dashboard/);
});
```

### Test Fixtures

Playwright provides built-in fixtures:

- `page` - A single tab/page
- `context` - A browser context with cookies and storage
- `browser` - The browser instance
- `browserName` - The name of the browser (chromium, firefox, webkit)

### Custom Fixtures

```javascript
import { test as base } from '@playwright/test';

const test = base.extend({
  authenticatedPage: async ({ page }, use) => {
    // Setup
    await page.goto('https://example.com/login');
    await page.fill('input[name="email"]', 'user@example.com');
    await page.fill('input[name="password"]', 'password');
    await page.click('button:has-text("Sign In")');
    await page.waitForNavigation();

    // Use the fixture
    await use(page);

    // Teardown (optional)
    await page.close();
  },
});

export { test };
```

## Selectors

### Locator Methods

```javascript
// CSS selector
page.locator('button.submit');

// XPath
page.locator('//button[contains(@class, "submit")]');

// Text matcher
page.locator('text=Login');

// Role-based (recommended)
page.locator('role=button[name="Login"]');

// Combining selectors
page.locator('article').locator('button').first();
```

### Recommended: Role-Based Selectors

Use role selectors for better maintainability:

```javascript
// Instead of:
page.locator('button.submit');

// Use:
page.locator('role=button[name="Submit"]');
```

### Creating Reusable Locators

```javascript
const loginButton = page.locator('role=button[name="Login"]');
await loginButton.click();
```

## Interactions

### Common Actions

```javascript
// Navigation
await page.goto('https://example.com');
await page.goBack();
await page.reload();

// Clicking
await page.click('button');
await page.locator('button').click({ button: 'right' }); // Right-click
await page.dblclick('button'); // Double-click

// Filling input
await page.fill('input[name="email"]', 'user@example.com');
await page.type('input[name="password"]', 'password'); // Type with delay

// Selecting options
await page.selectOption('select[name="country"]', 'US');

// Keyboard actions
await page.press('input', 'Enter');
await page.keyboard.type('Hello world');

// Scrolling
await page.locator('article').scrollIntoViewIfNeeded();
await page.evaluate(() => window.scrollBy(0, 1000));

// Waiting
await page.waitForNavigation();
await page.waitForLoadState('networkidle');
await page.waitForSelector('button:has-text("Sign In")');
```

## Assertions

### Common Assertions

```javascript
const button = page.locator('button');

// Visibility and existence
await expect(button).toBeVisible();
await expect(button).toBeHidden();
await expect(button).toBeEnabled();
await expect(button).toBeDisabled();

// Content and attributes
await expect(button).toHaveText('Click me');
await expect(button).toContainText('Click');
await expect(button).toHaveAttribute('aria-label', 'Submit form');
await expect(button).toHaveClass(/primary/);

// Page assertions
await expect(page).toHaveTitle('Home');
await expect(page).toHaveURL(/example.com/);

// Element count
await expect(page.locator('table tr')).toHaveCount(5);

// Custom assertions
await expect(async () => {
  const response = await page.evaluate(() => window.data);
  expect(response).toBe('expected value');
}).toPass();
```

## Running Tests

### Basic Commands

```bash
# Run all tests
npx playwright test

# Run specific test file
npx playwright test login.spec.js

# Run tests with pattern
npx playwright test --grep @smoke

# Run in headed mode (see browser)
npx playwright test --headed

# Run in single browser
npx playwright test --project=chromium

# Debug mode
npx playwright test --debug
```

### Useful Flags

```bash
--headed              # Show browser window
--headed --no-headed  # Override config
--workers=1           # Disable parallelization
--reporter=html       # Generate HTML report
--reporter=list       # Output test results to console
--trace=on            # Record test trace
--video=on            # Record video of test
--screenshot=on       # Take screenshots
```

## Debugging

### Debug Mode

```bash
npx playwright test --debug
```

Opens the Playwright Inspector where you can step through tests.

### Browser DevTools

```javascript
await page.pause(); // Pauses execution, opens browser DevTools
```

### Logging

```javascript
import { chromium } from '@playwright/test';

const browser = await chromium.launch({
  headless: false,
  slowMo: 1000, // Slow down by 1000ms
});

console.log('Test is running...'); // Use console for debugging
```

### Capturing Traces

```javascript
import { test, expect } from '@playwright/test';

test('example', async ({ page, context }) => {
  await context.tracing.start({ screenshots: true, snapshots: true });

  // Your test here
  await page.goto('https://example.com');

  await context.tracing.stop({ path: 'trace.zip' });
});
```

View traces with: `npx playwright show-trace trace.zip`

### Recording Videos and Screenshots

Configure in `playwright.config.js`:

```javascript
export default {
  use: {
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    trace: 'on-first-retry',
  },
};
```

## Best Practices

### 1. Use Data-Test Attributes

Add data-test attributes to your HTML:

```html
<button data-test="login-submit">Sign In</button>
```

Then in tests:

```javascript
await page.locator('[data-test="login-submit"]').click();
```

### 2. Avoid Hard Waits

```javascript
// ❌ Bad
await page.waitForTimeout(5000);

// ✅ Good
await page.waitForSelector('button:has-text("Submit")');
await page.locator('button:has-text("Submit")').isVisible();
```

### 3. Use Page Objects

```javascript
class LoginPage {
  constructor(page) {
    this.page = page;
    this.emailInput = page.locator('input[name="email"]');
    this.passwordInput = page.locator('input[name="password"]');
    this.submitButton = page.locator('role=button[name="Sign In"]');
  }

  async goto() {
    await this.page.goto('https://example.com/login');
  }

  async login(email, password) {
    await this.emailInput.fill(email);
    await this.passwordInput.fill(password);
    await this.submitButton.click();
  }
}

// In test
test('user can log in', async ({ page }) => {
  const loginPage = new LoginPage(page);
  await loginPage.goto();
  await loginPage.login('user@example.com', 'password');
  await expect(page).toHaveURL(/dashboard/);
});
```

### 4. Test One Thing Per Test

```javascript
// ❌ Bad - Multiple assertions
test('user journey', async ({ page }) => {
  await page.goto('https://example.com');
  await expect(page).toHaveTitle('Home');
  // ... login steps ...
  await expect(page).toHaveURL(/dashboard/);
  // ... more steps ...
});

// ✅ Good - Single focus
test('homepage loads correctly', async ({ page }) => {
  await page.goto('https://example.com');
  await expect(page).toHaveTitle('Home');
});
```

### 5. Use Test Tags

```javascript
test('user can log in', async ({ page }) => {
  // test code
});

test('@smoke user can log in', async ({ page }) => {
  // test code
});

test('@regression user can log in', async ({ page }) => {
  // test code
});
```

Run specific tags:

```bash
npx playwright test --grep @smoke
```

## Advanced Topics

### Network Mocking

```javascript
// Mock API response
await page.route('**/api/user', route => {
  route.abort();
});

// Intercept and modify
await page.route('**/api/data', route => {
  route.continue({
    postData: JSON.stringify({ modified: true }),
  });
});

// Return custom response
await page.route('**/api/user', route => {
  route.fulfill({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify({ id: 1, name: 'Test User' }),
  });
});
```

### Handling Multiple Pages

```javascript
test('open link in new tab', async ({ context, page }) => {
  await page.goto('https://example.com');

  const [newPage] = await Promise.all([
    context.waitForEvent('page'),
    page.click('a[target="_blank"]'),
  ]);

  await expect(newPage).toHaveURL(/example.com/);
});
```

### Performance Testing

```javascript
import { test, expect } from '@playwright/test';

test('page loads quickly', async ({ page }) => {
  const startTime = Date.now();

  await page.goto('https://example.com');
  await page.waitForLoadState('networkidle');

  const loadTime = Date.now() - startTime;
  expect(loadTime).toBeLessThan(3000); // Less than 3 seconds
});
```

### Accessibility Testing

```javascript
import { injectAxe, checkA11y } from 'axe-playwright';

test('page is accessible', async ({ page }) => {
  await page.goto('https://example.com');
  await injectAxe(page);
  await checkA11y(page);
});
```

## Troubleshooting

### Tests Are Flaky

**Problem**: Tests pass sometimes but fail others

**Solutions**:
- Replace hard waits with explicit waits
- Use stable selectors (data-test attributes)
- Ensure proper synchronization with page loads
- Check for race conditions

### Browser Won't Open

**Problem**: "Browser closed" error

**Solutions**:
```bash
# Reinstall browsers
npx playwright install

# Clear browser cache
npx playwright install --with-deps
```

### Selector Not Found

**Problem**: "Target page, context or browser has been closed"

**Solutions**:
```javascript
// Use locator instead of deprecated selectors
// ❌ Old way
page.$('button');

// ✅ New way
page.locator('button');
```

### Tests Timeout

**Problem**: Tests exceed timeout

**Solutions**:
```javascript
test('slow test', async ({ page }) => {
  test.setTimeout(60000); // Set to 60 seconds
  // test code
});
```

Or in config:

```javascript
export default {
  timeout: 30000,
  expect: { timeout: 5000 },
};
```

## Configuration

### playwright.config.js

```javascript
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  testMatch: '**/*.spec.js',
  timeout: 30000,
  expect: { timeout: 5000 },
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,

  reporter: 'html',

  use: {
    baseURL: 'http://localhost:3000',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },

  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
    { name: 'firefox', use: { ...devices['Desktop Firefox'] } },
    { name: 'webkit', use: { ...devices['Desktop Safari'] } },
    { name: 'Mobile Chrome', use: { ...devices['Pixel 5'] } },
    { name: 'Mobile Safari', use: { ...devices['iPhone 12'] } },
  ],

  webServer: {
    command: 'npm run start',
    url: 'http://localhost:3000',
    reuseExistingServer: !process.env.CI,
  },
});
```

## Resources

- [Official Documentation](https://playwright.dev)
- [API Reference](https://playwright.dev/docs/api/class-playwright)
- [Debugging Guide](https://playwright.dev/docs/debug)
- [Community GitHub](https://github.com/microsoft/playwright)

---

**Version**: 1.0  
**Last Updated**: 2024
