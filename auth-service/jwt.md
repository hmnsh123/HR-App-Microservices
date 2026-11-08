# JWT Implementation – HR Microservices

## 1. Architecture

We implemented JWT-based authentication between:

- **Auth Service:** Port 8081 – authenticates users and generates JWT.
- **Employee Service:** Port 8083 – validates JWT and protects employee APIs.

Both services use the same secret key to sign and verify tokens.

## 2. Request Flow

1. User sends email and password to Auth Service.
2. Auth Service validates credentials.
3. Auth Service generates JWT containing email and role.
4. Client receives the token.
5. Client sends the token with Employee Service requests:

   `Authorization: Bearer <token>`

6. `JwtAuthenticationFilter` extracts the token.
7. `JwtService` verifies its signature and expiration and extracts claims.
8. Filter stores user identity and role in `SecurityContextHolder`.
9. Spring Security checks permissions.
10. Employee Controller processes the request if authorized.

## 3. Important Classes

| Class | Responsibility |
|---|---|
| JwtService | Generates or parses JWT, depending on the service |
| JwtAuthenticationFilter | Extracts token and sets authentication |
| SecurityConfig | Defines endpoint access rules |
| SecurityContextHolder | Stores current request's authentication |

## 4. Role-Based Authorization

| Operation | EMPLOYEE | ADMIN |
|---|---|---|
| GET – Read | Allowed | Allowed |
| POST – Create | Denied | Allowed |
| PUT – Update | Denied | Allowed |
| PATCH – Update | Denied | Allowed |
| DELETE – Delete | Denied | Allowed |

## 5. Important HTTP Status Codes

- **200:** Request successful.
- **401 Unauthorized:** Missing, invalid, or expired authentication.
- **403 Forbidden:** User is authenticated but doesn't have permission.

## 6. JWT Structure

JWT contains three parts:

1. Header – algorithm and token type.
2. Payload – claims such as email, role, expiration.
3. Signature – verifies token integrity and authenticity.

JWT payload is Base64URL encoded, not encrypted. Never store passwords or sensitive information in it.

## 7. Important Concepts

- **Bearer Token:** Whoever possesses the token can present it for access.
- **HS256:** HMAC with SHA-256, a symmetric signing algorithm.
- **Base64:** Encoding, not encryption.
- **JJWT:** Java library used to generate and parse JWTs.
- **Stateless Authentication:** Server doesn't maintain an HTTP session for each user.
- **Authentication:** Who are you?
- **Authorization:** What are you allowed to do?
- **Expiration:** Token becomes invalid after its configured expiry.

Our token expiration is configured as `3600000` milliseconds (1 hour).

## 8. Security Configuration

- CSRF disabled for this stateless API setup.
- Session policy: `STATELESS`.
- JWT filter added before `UsernamePasswordAuthenticationFilter`.
- Role-based authorization configured using `hasRole()` and `hasAnyRole()`.

## 9. Final Testing Checklist

- No token → 401
- Invalid token → 401
- Modified token → 401
- Expired token → 401
- EMPLOYEE + GET → 200
- EMPLOYEE + POST → 403
- ADMIN + POST → 200

## 10. Interview Summary

"We implemented JWT-based authentication across our HR microservices. Auth Service validates user credentials and generates a signed JWT containing the user's email and role. Employee Service independently verifies the token using the shared secret key. A custom JWT filter extracts claims and sets authentication in Spring Security's SecurityContextHolder. We then enforce role-based authorization, allowing employees to read employee records while administrators can perform all CRUD operations."

