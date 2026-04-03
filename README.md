# Zorvyn
A finance dashboard system where different users interact with financial records based on their role.

# Core Features -

1. User & Role Management
Create and manage users
Assign roles to users
Maintain user status (active / inactive)
Enforce role-based restrictions

Roles (suggested):

Viewer → read-only access
Analyst → read + analytics
Admin → full access (users + records)

2. Financial Records Management
Each record should support:
amount
type (income / expense)
category
date
notes / description

Operations required:

Create records
Read records
Update records
Delete records
Filter records:
by date range
by category
by type

3. Dashboard Summary APIs
Provide aggregated data endpoints:
Total income
Total expenses
Net balance
Category-wise totals
Recent transactions
Time-based trends:
monthly
weekly

4. Access Control (RBAC)
Enforce permissions based on roles
Examples:
Viewer → cannot modify anything
Analyst → read + analytics only
Admin → full CRUD + user management
Implement via:
middleware / filters / guards (Spring Security)

5. Validation & Error Handling
Input validation (fields, formats, required values)
Proper HTTP status codes:
400 → bad request
401 → unauthorized
403 → forbidden
404 → not found
Meaningful error messages
Prevent invalid operations

6. Data Persistence
Use a database (MySQL is fine)
Store:
users
roles
financial records
Support efficient queries for:
filtering
aggregations
