# CampusConnect persistent media setup

The application now uploads media directly to S3-compatible object storage. It does not write uploaded post, marketplace, avatar, status, attachment, or voice files into the project `uploads/` directory.

## Cloudflare R2

Create an R2 bucket and an API token with object read/write access. If you use a custom R2 domain, set it as `STORAGE_PUBLIC_BASE_URL` so URLs saved in MySQL are permanent CDN URLs.

In Windows Command Prompt, set these before starting Spring Boot:

```cmd
set STORAGE_ENDPOINT=https://YOUR_ACCOUNT_ID.r2.cloudflarestorage.com
set STORAGE_BUCKET=campusconnect-media
set STORAGE_ACCESS_KEY=YOUR_R2_ACCESS_KEY
set STORAGE_SECRET_KEY=YOUR_R2_SECRET_KEY
set STORAGE_REGION=auto
set STORAGE_PUBLIC_BASE_URL=https://cdn.yourdomain.com
```

For AWS S3, leave `STORAGE_ENDPOINT` empty and use the bucket/CloudFront public URL for `STORAGE_PUBLIC_BASE_URL`.

## System accounts

On first startup, if they do not already exist, the application creates:

- `@campusconnect` — Campus Connect Official
- `@campusconnectadmin` — Campus Connect Admin

Existing reserved accounts are not overwritten.

Optional passwords:

```cmd
set SYSTEM_OFFICIAL_PASSWORD=YOUR_OFFICIAL_PASSWORD
set SYSTEM_ADMIN_PASSWORD=YOUR_ADMIN_PASSWORD
```

The passwords are only used when a system account does not already exist.

## Reserved namespace

Normal registrations and community/group/channel creation reject names containing the `campusconnect` namespace, including variations with spaces, punctuation, or capitalization.

## Message deletion

Message deletion is soft-delete only. The API never calls `messages.delete(...)`. It records deletion flags/timestamps and filters the deleted message from the deleting user's conversation view.

## UI palette

The UI uses:

- Primary: `#6D5DFC`
- Secondary: `#0EA5E9` (the supplied `#0EASE9` is not a valid CSS hex value, so it is corrected to the intended sky-blue value)
- Dark Neutral: `#111827`
- Light Background: `#FBFAFC`
- Surface White: `#FFFFFF`
