# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
mvnw.cmd clean install

# Run (dev profile is default)
mvnw.cmd spring-boot:run

# Run tests
mvnw.cmd test

# Run a single test class
mvnw.cmd test -Dtest=AccountServiceTest

# Package
mvnw.cmd clean package
```

## Architecture

**Peecko API** is a Spring Boot 3.2 / Java 17 REST API for a video streaming platform, backed by PostgreSQL (H2 for tests).

### Layer conventions

| Layer | Package | Role |
|---|---|---|
| Controllers | `web/rest/` | `@RestController`, extend `BaseResource` for `getUsername()` |
| Services | `service/` | Business logic, cache management |
| Repositories | `repository/` | Spring Data JPA, extend `JpaRepository` |
| Entities | `domain/` | JPA entities, enums in `domain/enumeration/` |
| DTOs | `domain/dto/` + `web/payload/` | `request/` for inbound, `response/` for outbound |
| Mappers | `domain/mapper/` | Entity ↔ DTO conversion |

### Security model

- **JWT authentication** — stateless, CSRF and CORS disabled.
- `InvalidJwtService` maintains a **JWT blacklist** for logout/token revocation.
- `@Licensed` (custom annotation + AspectJ AOP in `security/`) validates that a user holds an active membership before accessing protected endpoints.
- All controllers that require auth extend `BaseResource`, which reads the current principal from `SecurityContextHolder`.

### Domain concepts

- **ApsUser** — the platform user entity (not to be confused with Spring's `UserDetails`).
- **ApsMembership** — period-based license/subscription. The `@Licensed` AOP aspect checks this before allowing video access.
- **Video / VideoCategory / Coach** — core content entities. Videos carry `Lang`, `Intensity`, and `PlayerType` enumerations.
- **PlayList / PlayListItem** — user-created playlists; unique per (user, name) pair.
- **PinCode** — short-lived token for PIN-based secondary verification (e.g., email confirmation).
- **Label / Language** — multi-language UI strings stored in the DB, served via `LabelService`.

### Caching

Caffeine cache (1-hour TTL, 1 000 max entries) with named caches configured in `application.yml`:
`labels`, `videoTagLabels`, `intensityLabels`, `audienceLabels`, `todayVideos`, `videoLibrary`, `videosByCategory`.

`VideoService` and `LabelService` are the primary cache users — evict or refresh via their methods when content changes.

### Configuration profiles

| Profile | DB | Notes |
|---|---|---|
| `dev` (default) | H2 in-memory | Debug logging, localhost SMTP |
| `prod` | PostgreSQL | `smtpout.secureserver.net:465` |

Sensitive values (DB credentials, mail passwords, JWT secret) live in `application-dev.yml` / `application-prod.yml` — never commit real secrets to those files.

### Testing

- Integration tests only (`@SpringBootTest` + `@Transactional`), hitting H2.
- `EntityBuilder` / `EntityDefault` are the test-data factories — use them rather than constructing entities by hand.
- Tests run single-threaded (maven-surefire non-parallel) to avoid H2 conflicts.
