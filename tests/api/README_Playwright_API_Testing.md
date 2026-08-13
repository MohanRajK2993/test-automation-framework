# API Testing with Playwright

A comprehensive guide to API testing using Playwright's APIRequestContext, enabling cross-browser API validation without a UI.

## Table of Contents

- [Introduction](#introduction)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Making Requests](#making-requests)
- [Response Assertions](#response-assertions)
- [Authentication](#authentication)
- [Request Interception](#request-interception)
- [Running Tests](#running-tests)
- [Debugging](#debugging)
- [Best Practices](#best-practices)
- [Advanced Topics](#advanced-topics)
- [Troubleshooting](#troubleshooting)

## Introduction

Playwright's API testing capabilities allow you to:

- **Test APIs independently** without a browser
- **Fast execution** - no browser overhead
- **Full control** over requests and responses
- **Integration testing** - combine UI and API tests
- **Automation** - test workflows across API layers
- **Network interception** - mock and validate requests

### Key Advantages

- **No browser dependency** for pure API tests
- **Request/response inspection** and modification
- **Multi-step workflows** with shared context
- **File upload/download** testing
- **Network conditions** simulation
- **Authentication** handling (JWT, cookies, OAuth)

## Installation

### Prerequisites

- Node.js 16+
- npm or yarn

### Setup

1. **Install Playwright:**

```bash
npm install --save-dev @playwright/test
```

2. **Install browsers (optional for API-only tests):**

```bash
npx playwright install
```

## Quick Start

### Your First API Test

Create a file `api.spec.js`:

```javascript
import { test, expect } from '@playwright/test';

test('GET user by ID', async ({ request }) => {
  const response = await request.get('https://jsonplaceholder.typicode.com/users/1');
  expect(response.status()).toBe(200);
  
  const json = await response.json();
  expect(json.id).toBe(1);
  expect(json.name).toBeTruthy();
});
```

Run the test:

```bash
npx playwright test api.spec.js
```

## Making Requests

### HTTP Methods

#### GET Request

```javascript
test('fetch user data', async ({ request }) => {
  const response = await request.get('https://api.example.com/users/1');
  
  expect(response.status()).toBe(200);
  const data = await response.json();
  expect(data.id).toBe(1);
});
```

#### POST Request

```javascript
test('create new user', async ({ request }) => {
  const response = await request.post('https://api.example.com/users', {
    data: {
      name: 'John Doe',
      email: 'john@example.com',
      age: 30,
    },
  });
  
  expect(response.status()).toBe(201);
  const createdUser = await response.json();
  expect(createdUser.name).toBe('John Doe');
});
```

#### PUT Request

```javascript
test('update user', async ({ request }) => {
  const response = await request.put('https://api.example.com/users/1', {
    data: {
      name: 'Jane Doe',
      email: 'jane@example.com',
    },
  });
  
  expect(response.status()).toBe(200);
  const updatedUser = await response.json();
  expect(updatedUser.name).toBe('Jane Doe');
});
```

#### PATCH Request

```javascript
test('partially update user', async ({ request }) => {
  const response = await request.patch('https://api.example.com/users/1', {
    data: {
      name: 'Updated Name',
    },
  });
  
  expect(response.status()).toBe(200);
});
```

#### DELETE Request

```javascript
test('delete user', async ({ request }) => {
  const response = await request.delete('https://api.example.com/users/1');
  expect(response.status()).toBe(204);
});
```

### Request Options

```javascript
test('request with options', async ({ request }) => {
  const response = await request.get('https://api.example.com/users', {
    // Query parameters
    params: {
      page: 1,
      limit: 10,
      sort: 'name',
    },
    
    // Headers
    headers: {
      'Authorization': 'Bearer token123',
      'Content-Type': 'application/json',
      'X-Custom-Header': 'value',
    },
    
    // Timeout
    timeout: 10000,
    
    // Form data
    form: {
      username: 'john',
      password: 'secret',
    },
    
    // Multi-part form data
    multipart: {
      file: fs.createReadStream('path/to/file.txt'),
      name: 'John Doe',
    },
  });
});
```

## Response Assertions

### Status Codes

```javascript
test('validate response status', async ({ request }) => {
  const response = await request.get('https://api.example.com/users/1');
  
  expect(response.status()).toBe(200);
  expect(response.ok()).toBeTruthy();
});
```

### Headers

```javascript
test('validate response headers', async ({ request }) => {
  const response = await request.get('https://api.example.com/data');
  
  expect(response.headerValue('content-type')).toContain('application/json');
  expect(response.headers()['x-custom-header']).toBe('expected-value');
});
```

### JSON Response

```javascript
test('validate JSON response', async ({ request }) => {
  const response = await request.get('https://api.example.com/users/1');
  
  const json = await response.json();
  expect(json).toMatchObject({
    id: 1,
    name: expect.any(String),
    email: expect.stringContaining('@'),
  });
});
```

### Text Response

```javascript
test('validate text response', async ({ request }) => {
  const response = await request.get('https://api.example.com/content');
  
  const text = await response.text();
  expect(text).toContain('expected content');
  expect(text).toMatch(/pattern/);
});
```

### Binary Response

```javascript
test('validate binary response', async ({ request }) => {
  const response = await request.get('https://api.example.com/image.png');
  
  const buffer = await response.body();
  expect(buffer.length).toBeGreaterThan(0);
});
```

### Comprehensive Assertions

```javascript
test('comprehensive response validation', async ({ request }) => {
  const response = await request.post('https://api.example.com/users', {
    data: {
      name: 'John Doe',
      email: 'john@example.com',
    },
  });
  
  // Status
  expect(response.status()).toBe(201);
  expect(response.ok()).toBe(true);
  
  // Headers
  expect(response.headerValue('content-type')).toContain('application/json');
  
  // Body
  const json = await response.json();
  expect(json).toHaveProperty('id');
  expect(json.name).toBe('John Doe');
  expect(json.email).toBe('john@example.com');
  expect(json.createdAt).toBeTruthy();
});
```

## Authentication

### Bearer Token

```javascript
test('request with bearer token', async ({ request }) => {
  const response = await request.get('https://api.example.com/protected', {
    headers: {
      'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIs...',
    },
  });
  
  expect(response.ok()).toBeTruthy();
});
```

### API Key

```javascript
test('request with API key', async ({ request }) => {
  const response = await request.get('https://api.example.com/data', {
    params: {
      api_key: 'your-api-key-here',
    },
  });
  
  expect(response.ok()).toBeTruthy();
});
```

### Basic Auth

```javascript
test('request with basic authentication', async ({ request }) => {
  const response = await request.get('https://api.example.com/secure', {
    headers: {
      'Authorization': `Basic ${Buffer.from('username:password').toString('base64')}`,
    },
  });
  
  expect(response.ok()).toBeTruthy();
});
```

### OAuth / Login Flow

```javascript
test('OAuth login and use token', async ({ request }) => {
  // Step 1: Login to get token
  const loginResponse = await request.post('https://api.example.com/login', {
    data: {
      email: 'user@example.com',
      password: 'password123',
    },
  });
  
  const loginData = await loginResponse.json();
  const token = loginData.token;
  
  // Step 2: Use token in subsequent requests
  const response = await request.get('https://api.example.com/profile', {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  
  expect(response.ok()).toBeTruthy();
  const profile = await response.json();
  expect(profile.email).toBe('user@example.com');
});
```

### Reusable Authentication with Fixtures

```javascript
import { test as base } from '@playwright/test';

const test = base.extend({
  authenticatedRequest: async ({ request }, use) => {
    // Get authentication token
    const loginResponse = await request.post('https://api.example.com/login', {
      data: {
        email: 'test@example.com',
        password: 'password123',
      },
    });
    
    const { token } = await loginResponse.json();
    
    // Create a request context with auth header
    const authRequest = {
      get: (url, options = {}) => 
        request.get(url, {
          ...options,
          headers: {
            'Authorization': `Bearer ${token}`,
            ...options.headers,
          },
        }),
      post: (url, options = {}) => 
        request.post(url, {
          ...options,
          headers: {
            'Authorization': `Bearer ${token}`,
            ...options.headers,
          },
        }),
      put: (url, options = {}) => 
        request.put(url, {
          ...options,
          headers: {
            'Authorization': `Bearer ${token}`,
            ...options.headers,
          },
        }),
      delete: (url, options = {}) => 
        request.delete(url, {
          ...options,
          headers: {
            'Authorization': `Bearer ${token}`,
            ...options.headers,
          },
        }),
    };
    
    await use(authRequest);
  },
});

export { test };
```

## Request Interception

### Intercepting Requests in Browser Context

```javascript
test('intercept and mock API', async ({ page, context }) => {
  // Mock successful response
  await page.route('**/api/users/*', route => {
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        id: 1,
        name: 'Mocked User',
        email: 'mocked@example.com',
      }),
    });
  });
  
  await page.goto('https://example.com');
  // Application will use mocked data
});
```

### Intercepting and Modifying Requests

```javascript
test('intercept and modify request', async ({ page }) => {
  await page.route('**/api/data', route => {
    // Modify the request
    const request = route.request();
    route.continue({
      headers: {
        ...request.headers(),
        'X-Custom-Header': 'modified-value',
      },
    });
  });
  
  await page.goto('https://example.com');
});
```

### Conditional Interception

```javascript
test('selective request interception', async ({ page }) => {
  await page.route('**/api/**', route => {
    const request = route.request();
    
    if (request.method() === 'GET' && request.url().includes('/users')) {
      route.fulfill({
        status: 200,
        body: JSON.stringify([{ id: 1, name: 'User' }]),
      });
    } else if (request.url().includes('/error')) {
      route.abort('failed');
    } else {
      route.continue();
    }
  });
  
  await page.goto('https://example.com');
});
```

## Running Tests

### Basic Commands

```bash
# Run all API tests
npx playwright test

# Run specific test file
npx playwright test api.spec.js

# Run tests matching pattern
npx playwright test --grep "users"

# Run with verbose output
npx playwright test --reporter=verbose

# Run tests in parallel
npx playwright test --workers=4

# Run single-threaded (helps with debugging)
npx playwright test --workers=1
```

### Test Configuration

```bash
# Override timeout
npx playwright test --timeout=60000

# Retry failed tests
npx playwright test --retries=2

# Generate HTML report
npx playwright test --reporter=html

# Record test trace
npx playwright test --trace=on
```

## Debugging

### Enable Debugging

```bash
# Run with debug mode
npx playwright test --debug

# View test with trace
npx playwright show-trace trace.zip
```

### Console Logging

```javascript
test('debug with logging', async ({ request }) => {
  console.log('Starting test...');
  
  const response = await request.get('https://api.example.com/users/1');
  console.log('Response status:', response.status());
  
  const json = await response.json();
  console.log('Response body:', JSON.stringify(json, null, 2));
  
  expect(response.ok()).toBeTruthy();
});
```

### Request/Response Details

```javascript
test('log request and response details', async ({ request }) => {
  const response = await request.post('https://api.example.com/users', {
    data: {
      name: 'John Doe',
      email: 'john@example.com',
    },
  });
  
  // Log request details
  console.log('Method:', 'POST');
  console.log('URL:', response.url());
  console.log('Status:', response.status());
  
  // Log response headers
  console.log('Headers:', response.headers());
  
  // Log response body
  const json = await response.json();
  console.log('Body:', JSON.stringify(json, null, 2));
});
```

### Network Traffic Capture

```javascript
test('capture network traffic', async ({ request, context }) => {
  const requestLog = [];
  
  // Listen to all requests
  context.on('request', request => {
    requestLog.push({
      method: request.method(),
      url: request.url(),
      headers: request.headers(),
    });
  });
  
  // Make requests
  await request.get('https://api.example.com/users');
  
  console.log('Captured requests:', JSON.stringify(requestLog, null, 2));
});
```

## Best Practices

### 1. Organize Tests by Resource

```
tests/
├── users.spec.js
├── posts.spec.js
├── comments.spec.js
└── auth.spec.js
```

### 2. Use Page Objects for API

```javascript
// api-client.js
export class UserAPI {
  constructor(request, baseURL) {
    this.request = request;
    this.baseURL = baseURL;
  }
  
  async getUser(userId) {
    return this.request.get(`${this.baseURL}/users/${userId}`);
  }
  
  async createUser(userData) {
    return this.request.post(`${this.baseURL}/users`, {
      data: userData,
    });
  }
  
  async updateUser(userId, userData) {
    return this.request.put(`${this.baseURL}/users/${userId}`, {
      data: userData,
    });
  }
  
  async deleteUser(userId) {
    return this.request.delete(`${this.baseURL}/users/${userId}`);
  }
}

// In test
test('user workflow', async ({ request }) => {
  const api = new UserAPI(request, 'https://api.example.com');
  
  const createResponse = await api.createUser({
    name: 'John Doe',
    email: 'john@example.com',
  });
  
  const user = await createResponse.json();
  expect(user.id).toBeTruthy();
});
```

### 3. Test Data Management

```javascript
test('create and clean up test data', async ({ request }) => {
  // Setup: Create test data
  const createResponse = await request.post('https://api.example.com/users', {
    data: {
      name: 'Test User',
      email: `test-${Date.now()}@example.com`,
    },
  });
  
  const user = await createResponse.json();
  const userId = user.id;
  
  try {
    // Test operations
    const getResponse = await request.get(`https://api.example.com/users/${userId}`);
    expect(getResponse.ok()).toBeTruthy();
  } finally {
    // Cleanup: Delete test data
    await request.delete(`https://api.example.com/users/${userId}`);
  }
});
```

### 4. Separate Test and Production

```javascript
// playwright.config.js
export default {
  use: {
    baseURL: process.env.API_BASE_URL || 'https://staging-api.example.com',
  },
};
```

Run with:

```bash
API_BASE_URL=https://prod-api.example.com npx playwright test
```

### 5. Validate Error Scenarios

```javascript
test('handle API errors gracefully', async ({ request }) => {
  // Test 404
  const notFoundResponse = await request.get('https://api.example.com/users/99999');
  expect(notFoundResponse.status()).toBe(404);
  
  // Test validation error
  const invalidResponse = await request.post('https://api.example.com/users', {
    data: {
      name: '', // Invalid: empty name
    },
  });
  expect(invalidResponse.status()).toBe(400);
  
  const errorData = await invalidResponse.json();
  expect(errorData.errors).toBeTruthy();
});
```

### 6. Use Meaningful Test Names

```javascript
// ❌ Bad
test('test API', async ({ request }) => {
  // ...
});

// ✅ Good
test('should return 200 and user data when fetching existing user', async ({ request }) => {
  // ...
});
```

## Advanced Topics

### Multi-Step Workflows

```javascript
test('complete user registration and login flow', async ({ request }) => {
  // Step 1: Register
  const registerResponse = await request.post('https://api.example.com/register', {
    data: {
      email: 'newuser@example.com',
      password: 'SecurePassword123!',
      name: 'New User',
    },
  });
  expect(registerResponse.ok()).toBeTruthy();
  
  // Step 2: Verify email (mock)
  const verifyResponse = await request.post('https://api.example.com/verify-email', {
    data: {
      email: 'newuser@example.com',
      code: '123456',
    },
  });
  expect(verifyResponse.ok()).toBeTruthy();
  
  // Step 3: Login
  const loginResponse = await request.post('https://api.example.com/login', {
    data: {
      email: 'newuser@example.com',
      password: 'SecurePassword123!',
    },
  });
  
  const { token } = await loginResponse.json();
  expect(token).toBeTruthy();
  
  // Step 4: Access protected resource
  const profileResponse = await request.get('https://api.example.com/profile', {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  expect(profileResponse.ok()).toBeTruthy();
});
```

### File Upload Testing

```javascript
import fs from 'fs';

test('upload file via API', async ({ request }) => {
  const response = await request.post('https://api.example.com/upload', {
    multipart: {
      file: fs.createReadStream('path/to/test-file.txt'),
      description: 'Test file upload',
    },
  });
  
  expect(response.ok()).toBeTruthy();
  const data = await response.json();
  expect(data.fileUrl).toBeTruthy();
});
```

### File Download Testing

```javascript
import fs from 'fs';

test('download file from API', async ({ request }) => {
  const response = await request.get('https://api.example.com/export/report.pdf');
  
  expect(response.ok()).toBeTruthy();
  expect(response.headerValue('content-type')).toContain('application/pdf');
  
  const buffer = await response.body();
  fs.writeFileSync('downloaded-report.pdf', buffer);
});
```

### Performance Testing

```javascript
test('measure API response time', async ({ request }) => {
  const startTime = Date.now();
  
  const response = await request.get('https://api.example.com/large-dataset');
  
  const endTime = Date.now();
  const responseTime = endTime - startTime;
  
  expect(response.ok()).toBeTruthy();
  expect(responseTime).toBeLessThan(2000); // Should respond in < 2 seconds
  
  console.log(`Response time: ${responseTime}ms`);
});
```

### Concurrent Requests

```javascript
test('send concurrent requests', async ({ request }) => {
  const userIds = [1, 2, 3, 4, 5];
  
  const responses = await Promise.all(
    userIds.map(id => 
      request.get(`https://api.example.com/users/${id}`)
    )
  );
  
  responses.forEach(response => {
    expect(response.ok()).toBeTruthy();
  });
});
```

## Configuration

### playwright.config.js for API Testing

```javascript
import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  testMatch: '**/*api*.spec.js',
  timeout: 30000,
  expect: { timeout: 5000 },
  
  fullyParallel: true,
  workers: 4,
  
  retries: process.env.CI ? 2 : 0,
  
  reporter: [
    ['html'],
    ['json', { outputFile: 'test-results/results.json' }],
    ['junit', { outputFile: 'test-results/junit.xml' }],
  ],
  
  use: {
    // Base URL for all requests
    baseURL: process.env.API_BASE_URL || 'https://api.example.com',
    
    // Headers sent with every request
    extraHTTPHeaders: {
      'User-Agent': 'Playwright API Tests',
    },
    
    // Request timeout
    timeout: 10000,
    
    // Trace for debugging
    trace: 'on-first-retry',
  },
  
  // Global setup
  globalSetup: require.resolve('./global-setup.js'),
  globalTeardown: require.resolve('./global-teardown.js'),
});
```

### Global Setup and Teardown

```javascript
// global-setup.js
import { chromium } from '@playwright/test';

async function globalSetup() {
  console.log('Starting test suite...');
  
  // Could be used to initialize test data
  const browser = await chromium.launch();
  const context = await browser.createBrowserContext();
  
  // Perform setup actions if needed
  
  await context.close();
  await browser.close();
}

export default globalSetup;
```

## Troubleshooting

### Connection Refused

**Problem**: Cannot connect to API server

**Solution**:
```javascript
// Check baseURL in config
// Ensure API is running and accessible
// Use correct hostname/port

const response = await request.get('http://localhost:3000/api/users');
```

### Certificate Errors (HTTPS)

**Problem**: "SSL: CERTIFICATE_VERIFY_FAILED"

**Solution**:
```javascript
// For testing only - not for production
const response = await request.get('https://api.example.com/data', {
  ignoreHTTPSErrors: true,
});
```

### Timeout Issues

**Problem**: Tests timeout waiting for response

**Solution**:
```javascript
// Increase timeout
test('slow endpoint', async ({ request }) => {
  test.setTimeout(60000); // 60 seconds
  
  const response = await request.get('https://api.example.com/slow-endpoint', {
    timeout: 30000, // 30 seconds per request
  });
  
  expect(response.ok()).toBeTruthy();
});
```

### Rate Limiting

**Problem**: 429 Too Many Requests

**Solution**:
```javascript
test('respect rate limits', async ({ request }) => {
  for (let i = 0; i < 5; i++) {
    const response = await request.get('https://api.example.com/data');
    
    if (response.status() === 429) {
      // Wait before retrying
      await new Promise(resolve => setTimeout(resolve, 1000));
    }
  }
});
```

### Flaky Tests

**Problem**: Tests pass sometimes, fail other times

**Solutions**:
```javascript
// Wait for expected state
await expect(async () => {
  const response = await request.get('https://api.example.com/status');
  const data = await response.json();
  expect(data.status).toBe('ready');
}).toPass();

// Or use polling
async function waitForStatus(request, expectedStatus) {
  let attempts = 0;
  while (attempts < 30) {
    const response = await request.get('https://api.example.com/status');
    const data = await response.json();
    
    if (data.status === expectedStatus) {
      return data;
    }
    
    await new Promise(resolve => setTimeout(resolve, 100));
    attempts++;
  }
  
  throw new Error(`Status never reached ${expectedStatus}`);
}
```

### Invalid JSON Response

**Problem**: Parsing non-JSON as JSON

**Solution**:
```javascript
test('handle various response types', async ({ request }) => {
  const response = await request.get('https://api.example.com/data');
  
  const contentType = response.headerValue('content-type');
  
  if (contentType?.includes('application/json')) {
    const json = await response.json();
    expect(json).toHaveProperty('id');
  } else if (contentType?.includes('text/html')) {
    const html = await response.text();
    expect(html).toContain('expected-text');
  }
});
```

## Environment Variables

### .env.test

```bash
API_BASE_URL=https://staging-api.example.com
API_KEY=test-api-key-123
TEST_USER_EMAIL=testuser@example.com
TEST_USER_PASSWORD=testpassword123
```

### Access in Tests

```javascript
test('use environment variables', async ({ request }) => {
  const baseURL = process.env.API_BASE_URL;
  const apiKey = process.env.API_KEY;
  
  const response = await request.get(`${baseURL}/data`, {
    params: {
      api_key: apiKey,
    },
  });
  
  expect(response.ok()).toBeTruthy();
});
```

Run with:

```bash
# Load .env.test
export $(cat .env.test | xargs)
npx playwright test
```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: API Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      
      - run: npm ci
      - run: npx playwright install --with-deps
      
      - run: npx playwright test
      
      - uses: actions/upload-artifact@v3
        if: always()
        with:
          name: playwright-report
          path: playwright-report/
          retention-days: 30
```

## Resources

- [Official Documentation](https://playwright.dev/docs/api-testing)
- [APIRequestContext API](https://playwright.dev/docs/api/class-apirequestcontext)
- [GitHub Examples](https://github.com/microsoft/playwright/tree/main/tests/playwright-test/fixtures)
- [Community Discussions](https://github.com/microsoft/playwright/discussions)

---

**Version**: 1.0  
**Last Updated**: 2024
