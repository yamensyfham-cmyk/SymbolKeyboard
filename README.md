# SymbolKeyboard

A custom Android keyboard app with 5000+ Unicode symbols — zero letters, zero emojis. Built with Kotlin, Jetpack Compose, and Material3.

[![Build](https://github.com/your-username/SymbolKeyboard/actions/workflows/release.yml/badge.svg)](https://github.com/your-username/SymbolKeyboard/actions/workflows/release.yml)

## Features

- **5000+ Unicode symbols** across categories: crosses & religious, animals, ancient scripts, alchemical, chess, musical notation, astrological signs, arrows, geometric shapes, decorative dingbats, and more
- **Ancient script support** with embedded Noto fonts: Egyptian Hieroglyphs, Cuneiform, Phoenician, Runic, Gothic, Ogham, Linear A/B, Anatolian Hieroglyphs
- **Search** across all symbols by name
- **Favorites** system with long-press to add/remove
- **Recent symbols** row for quick access
- **Tabbed category browser** with LazyVerticalGrid
- **Material3 theming** with dynamic color support (Android 12+)
- **Settings** screen for toggling categories, theme, vibration, and sound
- **Secure** — refuses to activate in password fields
- **Accessible** — TalkBack content descriptions on all symbol buttons

## Project Structure

```
SymbolKeyboard/
├── app/                          # Android app module
│   └── src/main/java/com/symbolkeyboard/
│       ├── di/                   # Hilt dependency injection
│       ├── data/                 # Room, DataStore, Repository
│       ├── keyboard/             # InputMethodService + ViewModel
│       ├── ui/                   # Compose UI (theme, keyboard, settings)
│       └── util/                 # SymbolParser, PowerSaver, Constants
├── scripts/
│   ├── generate_symbols.py       # Unicode database generator
│   └── gen-debug-keystore.sh     # Debug keystore helper
├── cached/
│   └── UnicodeData.txt           # Fallback for offline builds
├── .github/workflows/release.yml # CI/CD pipeline
└── gradle/libs.versions.toml     # Version catalog
```

## Requirements

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android SDK 35
- Python 3.8+ (for symbol database generation)

## Setup

### 1. Clone and open

```bash
git clone https://github.com/your-username/SymbolKeyboard.git
cd SymbolKeyboard
```

Open the project in Android Studio and let it sync Gradle.

### 2. Generate debug keystore

```bash
chmod +x scripts/gen-debug-keystore.sh
./scripts/gen-debug-keystore.sh
```

This creates `~/.android/debug.keystore` with the standard debug credentials.

### 3. Generate symbol database

```bash
python scripts/generate_symbols.py
```

This downloads the latest UnicodeData.txt from unicode.org, parses it, and outputs `app/src/main/assets/symbols.json`.

### 4. Build and run

```bash
./gradlew installDebug
```

On first run, enable the keyboard in **Settings → System → Languages & input → Virtual keyboard → SymbolKeyboard**.

## Unicode Database Fallback

The symbol database is generated at build time by `scripts/generate_symbols.py`:

1. Downloads `UnicodeData.txt` from `https://www.unicode.org/Public/UCD/latest/ucd/UnicodeData.txt`
2. Parses the file and extracts symbols from target Unicode blocks
3. Outputs a minified `symbols.json` to `app/src/main/assets/symbols.json`

**If unicode.org is unreachable during build**, the script falls back to `cached/UnicodeData.txt` committed in the repository. To update the cached file:

```bash
curl -o cached/UnicodeData.txt https://www.unicode.org/Public/UCD/latest/ucd/UnicodeData.txt
```

## Signing

The project includes a release keystore at `keystore/symbolkeyboard.p12` with the following credentials:

- **Store password**: `symbolkeyboard`
- **Key alias**: `symbolkeyboard`
- **Key password**: `symbolkeyboard`

This keystore is committed to the repository so builds work out of the box. For production releases on Google Play, replace it with your own keystore.

## Why Some Recipients See Empty Boxes

Ancient scripts (Egyptian Hieroglyphs, Cuneiform, etc.) require specialized fonts that most devices don't include by default.

**This is the keyboard's UI**: The keyboard embeds Noto fonts for these scripts, so they display correctly *on the keyboard itself*.

**Recipient devices**: When you send a hieroglyph or cuneiform character via a messaging app, the recipient's device needs to have the corresponding font installed to render it. If they don't, they will see an empty box (□) or a question mark (?).

**This is not a keyboard bug.** It is a font limitation on the recipient's device. The keyboard correctly inserts the Unicode character; the recipient's system is responsible for rendering it.

### Affected Scripts

| Script | Unicode Block | Required Font |
|---|---|---|
| Egyptian Hieroglyphs | U+13000–U+1342F | Noto Sans Egyptian Hieroglyphs |
| Cuneiform | U+12000–U+123FF | Noto Sans Cuneiform |
| Phoenician | U+10900–U+1091F | Noto Sans Phoenician |
| Runic | U+16A0–U+16FF | Noto Sans Runic |
| Gothic | U+10330–U+1034F | Noto Sans Gothic |
| Ogham | U+1680–U+169F | Noto Sans Ogham |
| Linear A | U+10600–U+1077F | Noto Sans Linear A |
| Linear B | U+10000–U+1007F | Noto Sans Linear B |
| Anatolian Hieroglyphs | U+14400–U+1467F | Noto Sans Anatolian Hieroglyphs |

## Compatibility Notes

### WhatsApp / Messenger / Telegram / Instagram / Discord

SymbolKeyboard inserts characters using `commitText()` with pure Unicode strings. This works correctly in all modern messaging apps.

- **WhatsApp**: Full support for all Unicode symbols. Ancient scripts may show as boxes on the recipient's device.
- **Telegram**: Full support. Telegram's desktop app has better font coverage than mobile.
- **Discord**: Full support on all platforms.
- **Instagram**: Full support in stories, bio, comments, and DMs.
- **Messenger**: Full support.

### Known Limitations

- **Google Messages**: Some RCS-enhanced conversations may convert certain symbols. This is a server-side behavior change, not a keyboard issue.
- **Twitter/X**: Supports all Unicode symbols but character count varies (most symbols count as 2 characters).
- **Email clients**: Most support full Unicode. Some older email clients may not render ancient scripts.

## Performance

SymbolKeyboard handles 5000+ symbols efficiently through:

- **LazyVerticalGrid**: Only visible symbols are composed
- **Room database**: Symbol data is cached locally on first launch
- **Background parsing**: JSON is parsed on `Dispatchers.IO`
- **ViewModel state**: Survives configuration changes

## Building a Release

### Manual build:

```bash
# Generate symbols
python scripts/generate_symbols.py

# Build release APK
./gradlew assembleRelease

# The APK is at: app/build/outputs/apk/release/app-release.apk
```

### CI/CD (automatic):

Push a version tag:

```bash
git tag v1.0.0
git push origin v1.0.0
```

The GitHub Actions workflow will:
1. Generate the symbol database
2. Build a signed release APK
3. Verify the APK signature
4. Create a GitHub Release with the APK attached

## APK Size

The embedded Noto fonts for ancient scripts add approximately 15–25 MB to the APK. The app supports Android App Bundle (AAB) format, which allows Play Store to deliver only the required resources.

- **Debug APK**: ~35 MB
- **Release APK (with ProGuard)**: ~25 MB
- **AAB (Play Store)**: ~8 MB download per device

## Minimum SDK

- **API 24** (Android 7.0): Minimum supported version
- **API 26** (Android 8.0): Recommended minimum for best ancient script rendering
- **API 31** (Android 12): Required for Material You dynamic colors

## Troubleshooting

### Build fails with "Unicode data download failed"

```bash
# Check internet connection, or use cached file:
curl -o cached/UnicodeData.txt https://www.unicode.org/Public/UCD/latest/ucd/UnicodeData.txt
python scripts/generate_symbols.py
```

### Gradle out of memory

Increase heap in `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
```

### APK signing fails in CI

- Verify that `KEYSTORE_BASE64` is a valid base64-encoded keystore
- Ensure `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` match the keystore
- The keystore must use RSA key algorithm

### Keyboard not showing in app list

1. Go to **Settings → System → Languages & input → On-screen keyboard**
2. Tap **Manage on-screen keyboards**
3. Enable **SymbolKeyboard**

### Some symbols display as boxes on my device

Install a font that covers the missing Unicode range. For ancient scripts, install the corresponding Noto font from Google Fonts.

### Favorites missing after update

Favorites are stored in the Room database with proper migration support. If favorites are lost after an update, clear the app data and restart. This should not happen with correctly versioned migrations.

### Compose crashes with font errors

If an embedded font file fails to load, the app falls back to `FontFamily.Default`. Clear app data or reinstall if you experience persistent crashes.

## License

This project is licensed under the MIT License.

## Symbol Categories

| Category | Symbol Count | Unicode Blocks |
|---|---|---|
| Crosses & Religious | ~100 | Miscellaneous Symbols, Alchemical |
| Animals | ~200 | Misc Symbols and Pictographs |
| Ancient Scripts | ~5000+ | Egyptian Hieroglyphs, Cuneiform, etc. |
| Alchemical | ~100 | Alchemical Symbols |
| Chess | ~100 | Chess Symbols |
| Musical | ~200 | Musical Symbols |
| Astrological | ~100 | Misc Symbols |
| Arrows | ~300 | Arrows, Supplemental Arrows |
| Geometric | ~200 | Geometric Shapes |
| Dingbats | ~200 | Dingbats, Ornamental Dingbats |
| Miscellaneous | ~500 | Various blocks |

*Actual counts depend on the Unicode version at build time.*
