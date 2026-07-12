# PointsAPI2

Paper 26.1.2-compatible points/currency plugin with YAML and MySQL/MariaDB storage.

## PlaceholderAPI

If PlaceholderAPI is installed, PointsAPI automatically registers its built-in expansion at startup. Nothing needs to be downloaded or installed through eCloud. Use `%pointsapi_<currency>%`, for example `%pointsapi_gems%` or `%pointsapi_tokens%`.

## Installation

Requires Paper 26.1.2 and Java 25. Place `PointsAPI2-2.3.0.jar` in the server's `plugins` directory and restart the server.

## Messages

Every player-facing message is in `plugins/PointsAPI/messages.yml`. Edit the file, then run `/points reload` (or restart the server) to apply it. Use `&` for colour codes. The available replacements are documented at the top of that file.

## MySQL / MariaDB

The default storage is local YAML. To use MySQL or MariaDB, set `storage.type: MYSQL` in `plugins/PointsAPI/config.yml`, then provide the host, port, database, username, password, and any optional settings under `storage.mysql`. The plugin creates the `${table-prefix}balances` table automatically. Set `storage.type: YAML` to keep the legacy local file storage.

The MySQL connector and HikariCP connection pool are bundled in the release JAR; no extra server libraries are required.

Hello there! Looking for one of the most easy-to-use Bukkit points plugins without the support of vault? Want complete customization over your points? This is the plugin for you! Complete customization over the whole front-end.

Why choose this plugin over others?
- We offer our own API right out of the box, with many different ways of modifying player points.
- Easy to configure, just go to your config.yml and set it up!
- Easy permissions
- Set points, add points, deduct/take points, all through one command that's very easy to remember!
- Multiple currencies!
- Completely compatible with UUID
- Offline player support

FULL SUPPORT AND DOCUMENTATION: https://www.z609.me/threads/pointsapi2.7/

Permissions:
- /points | points.points, points.*, or OP

Commands:
- /points - Set points, add points, reset points, and deduct points for players!

Hope you all enjoy!
