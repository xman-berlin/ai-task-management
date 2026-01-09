# Environment Setup Guide

This guide shows you how to securely configure AI provider credentials on macOS.

## Quick Setup (Recommended: direnv)

### 1. Install direnv
```bash
brew install direnv
```

### 2. Add to your shell (one-time setup)

**For zsh (macOS default):**
```bash
echo 'eval "$(direnv hook zsh)"' >> ~/.zshrc
source ~/.zshrc
```

**For bash:**
```bash
echo 'eval "$(direnv hook bash)"' >> ~/.bash_profile
source ~/.bash_profile
```

### 3. Configure your API keys

Edit `.envrc` (already created, gitignored):
```bash
cd /Users/xman/projects/test/spring-boot-4-test
nano .envrc  # or use your favorite editor
```

Uncomment and fill in ONE provider section. For example:

**For OpenAI:**
```bash
export AI_API_KEY="sk-your-real-key-here"
export AI_BASE_URL="https://api.openai.com/v1"
export AI_MODEL="gpt-4-turbo-preview"
```

**For Grok:**
```bash
export AI_API_KEY="xai-your-real-key-here"
export AI_BASE_URL="https://api.x.ai/v1"
export AI_MODEL="grok-beta"
```

**For Local Ollama (no API key needed!):**
```bash
export AI_BASE_URL="http://localhost:11434/v1"
export AI_MODEL="llama2"
```

### 4. Allow direnv to load the file
```bash
direnv allow
```

### 5. Verify it worked
```bash
env | grep AI_
```

You should see your AI configuration variables.

### 6. Run the app
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Now when you click "AI Suggest", it will use your configured provider!

---

## Alternative: macOS Keychain (Global Storage)

If you prefer storing keys in macOS Keychain instead of project-specific `.envrc`:

### Store keys in Keychain
```bash
# Store OpenAI key
security add-generic-password -a "$USER" -s AI_API_KEY -w "sk-your-key-here"

# Store Grok key (if using Grok instead)
security add-generic-password -a "$USER" -s AI_API_KEY -w "xai-your-key-here"
```

### Load from Keychain when running app
```bash
export AI_API_KEY="$(security find-generic-password -a "$USER" -s AI_API_KEY -w)"
export AI_BASE_URL="https://api.openai.com/v1"
export AI_MODEL="gpt-4-turbo-preview"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Auto-load on shell startup (optional)
Add to `~/.zshrc`:
```bash
export AI_API_KEY="$(security find-generic-password -a "$USER" -s AI_API_KEY -w 2>/dev/null)"
export AI_BASE_URL="https://api.openai.com/v1"
export AI_MODEL="gpt-4-turbo-preview"
```

---

## Alternative: Manual Export (Quick Test)

For quick testing without installing direnv:

```bash
# Set for current terminal session only
export AI_API_KEY="sk-your-key"
export AI_BASE_URL="https://api.openai.com/v1"
export AI_MODEL="gpt-4-turbo-preview"

# Run app
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

⚠️ **Note**: These variables disappear when you close the terminal.

---

## Getting API Keys

### OpenAI
1. Go to https://platform.openai.com/api-keys
2. Click "Create new secret key"
3. Copy the key (starts with `sk-...`)

### Grok (xAI)
1. Go to https://console.x.ai/
2. Sign up/login
3. Create API key
4. Copy the key (starts with `xai-...`)

### OpenRouter
1. Go to https://openrouter.ai/
2. Sign in with GitHub
3. Go to Keys → Create new key
4. Copy the key (starts with `sk-or-...`)

### Ollama (Local, Free)
1. Install: `brew install ollama`
2. Start: `ollama serve` (in background)
3. Pull model: `ollama pull llama2`
4. No API key needed!

---

## Security Checklist

✅ `.envrc` is in `.gitignore`  
✅ Never commit real API keys  
✅ Use `.envrc.example` for sharing config templates  
✅ Rotate keys if accidentally exposed  
✅ Use different keys for dev/prod  

---

## Troubleshooting

### "direnv: error .envrc is blocked"
```bash
direnv allow
```

### Environment variables not loading
```bash
# Check if direnv is installed
which direnv

# Check if hook is in shell config
cat ~/.zshrc | grep direnv

# Reload shell
source ~/.zshrc
```

### API key not found / 401 error
```bash
# Verify variables are set
env | grep AI_

# If empty, reload direnv
direnv reload

# Or manually export
export AI_API_KEY="your-key"
```

### Wrong provider being used
```bash
# Check current config
echo $AI_BASE_URL
echo $AI_MODEL

# Update in .envrc and reload
direnv allow
```

---

## Next Steps

1. ✅ Set up your preferred AI provider using steps above
2. ✅ Run the app: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
3. ✅ Test "AI Suggest" button at http://localhost:18080
4. ✅ Commit your changes (keys are safely gitignored!)

For more provider options, see `AI_PROVIDERS_GUIDE.md`.

