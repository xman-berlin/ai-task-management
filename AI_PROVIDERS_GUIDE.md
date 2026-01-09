# AI Provider Configuration Guide

This project uses **Spring AI** which supports multiple AI providers through a unified API. You can easily switch between OpenAI, GitHub Copilot, Grok, or local models.

## Quick Start Options

### Option 1: GitHub Copilot (Recommended - Free for many users!)

If you have GitHub Copilot, you can use it for free:

```bash
# No API key needed if using Copilot in IDE
# Spring AI will auto-detect and use your Copilot session
export AI_MODEL=gpt-4
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option 2: OpenAI

```bash
export AI_API_KEY="sk-your-openai-key"
export AI_BASE_URL="https://api.openai.com"
export AI_MODEL="gpt-4-turbo-preview"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option 3: Grok (xAI)

```bash
export AI_API_KEY="xai-your-grok-key"
export AI_BASE_URL="https://api.x.ai"
export AI_MODEL="grok-beta"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option 4: Azure OpenAI

```bash
export AI_API_KEY="your-azure-key"
export AI_BASE_URL="https://your-instance.openai.azure.com"
export AI_MODEL="gpt-4"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option 5: OpenRouter (Access to 100+ models)

```bash
export AI_API_KEY="sk-or-your-openrouter-key"
export AI_BASE_URL="https://openrouter.ai/api/v1"
export AI_MODEL="anthropic/claude-3-opus"  # or any other model
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option 6: Local LLM (Ollama, LM Studio, etc.)

```bash
export AI_BASE_URL="http://localhost:11434/v1"  # Ollama default
export AI_MODEL="llama2"  # or mistral, codellama, etc.
# No API key needed for local models
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Configuration Details

### Environment Variables

| Variable | Description | Default | Example |
|----------|-------------|---------|---------|
| `AI_API_KEY` | API key for the provider | `sk-dummy-key-for-local-dev` | `sk-proj-...` |
| `AI_BASE_URL` | API endpoint URL | `https://api.openai.com` | `https://api.x.ai` |
| `AI_MODEL` | Model name to use | `gpt-4-turbo-preview` | `grok-beta` |

### application-dev.yml

You can also configure directly in the YAML file:

```yaml
spring:
  ai:
    openai:
      api-key: ${AI_API_KEY:your-key-here}
      base-url: ${AI_BASE_URL:https://api.openai.com}
      chat:
        options:
          model: ${AI_MODEL:gpt-4-turbo-preview}
          temperature: 0.7
          max-tokens: 500
```

## Provider-Specific Setup

### GitHub Copilot Setup

**Prerequisites:**
- GitHub Copilot subscription (free for students, teachers, open-source maintainers)
- GitHub Copilot extension installed in your IDE

**Steps:**
1. Make sure Copilot is active in your IDE
2. Spring AI will automatically detect and use your Copilot session
3. No API key needed!
4. Just run the app:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

### Grok (xAI) Setup

**Get API Key:**
1. Go to https://console.x.ai/
2. Create an account
3. Generate API key
4. Copy the key (starts with `xai-...`)

**Configure:**
```bash
export AI_API_KEY="xai-your-key"
export AI_BASE_URL="https://api.x.ai"
export AI_MODEL="grok-beta"
```

**Pricing:** Currently in beta, check https://x.ai/api for latest pricing

### OpenRouter Setup

**Why OpenRouter?**
- Access 100+ models through one API
- Pay-as-you-go pricing
- No subscriptions needed
- Support for Claude, Gemini, Llama, and more

**Get API Key:**
1. Go to https://openrouter.ai/
2. Sign in with GitHub
3. Go to Keys → Create new key
4. Copy the key (starts with `sk-or-...`)

**Configure:**
```bash
export AI_API_KEY="sk-or-your-key"
export AI_BASE_URL="https://openrouter.ai/api/v1"
export AI_MODEL="anthropic/claude-3-haiku"  # Cheap and fast
```

**Available Models:**
- `anthropic/claude-3-opus` - Most capable
- `anthropic/claude-3-haiku` - Fast and cheap
- `google/gemini-pro` - Google's model
- `meta-llama/llama-3-70b` - Open source
- And 100+ more!

### Local LLM Setup (Ollama)

**Prerequisites:**
1. Install Ollama: https://ollama.ai/
2. Pull a model:
   ```bash
   ollama pull llama2
   # or
   ollama pull mistral
   ollama pull codellama
   ```

**Configure:**
```bash
export AI_BASE_URL="http://localhost:11434/v1"
export AI_MODEL="llama2"
# No API key needed!
```

**Benefits:**
- ✅ Free
- ✅ Private (data never leaves your machine)
- ✅ No internet required after download
- ✅ Fast on local hardware

## Model Recommendations

### For Production (Best Quality)
- **OpenAI GPT-4 Turbo** - Most accurate, expensive (~$0.01/1K tokens)
- **Grok Beta** - Fast and capable, xAI's flagship
- **Claude 3 Opus** - Excellent reasoning, via OpenRouter

### For Development (Good Balance)
- **GPT-3.5 Turbo** - Fast and cheap (~$0.001/1K tokens)
- **Claude 3 Haiku** - Fast and affordable via OpenRouter
- **Grok Beta** - Good quality, competitive pricing

### For Local/Private (Free)
- **Llama 2** - Meta's open model via Ollama
- **Mistral** - Excellent open-source model
- **CodeLlama** - Optimized for code tasks

## Cost Comparison

| Provider | Model | Cost per 1K tokens | Cost per 100 suggestions* |
|----------|-------|-------------------|------------------------|
| GitHub Copilot | GPT-4 | Free** | $0.00 |
| OpenAI | GPT-4 Turbo | ~$0.01 | ~$0.40 |
| OpenAI | GPT-3.5 Turbo | ~$0.001 | ~$0.04 |
| Grok | grok-beta | ~$0.005 | ~$0.20 |
| OpenRouter | Claude Haiku | ~$0.0005 | ~$0.02 |
| Ollama | Llama 2 | $0 | $0.00 |

*Estimated based on ~400 tokens per suggestion
**Free with Copilot subscription ($10/month for individuals, free for students/OSS)

## Testing Different Providers

### Quick Test Script

```bash
# Test OpenAI
export AI_API_KEY="sk-your-openai-key"
export AI_MODEL="gpt-4-turbo-preview"
curl -X POST http://localhost:18080/api/ai/prioritize \
  -H "Content-Type: application/json" \
  -d '{"title": "Deploy to production", "description": "Critical bug fix", "dueDate": "2026-01-10T17:00:00"}'

# Test Grok
export AI_API_KEY="xai-your-grok-key"
export AI_BASE_URL="https://api.x.ai"
export AI_MODEL="grok-beta"
# Restart app, then test again

# Test Local (Ollama)
export AI_BASE_URL="http://localhost:11434/v1"
export AI_MODEL="llama2"
# Restart app, then test again
```

## Troubleshooting

### "AI unavailable" in rationale

**Check provider connectivity:**
```bash
# Test OpenAI
curl https://api.openai.com/v1/models \
  -H "Authorization: Bearer $AI_API_KEY"

# Test Grok
curl https://api.x.ai/v1/models \
  -H "Authorization: Bearer $AI_API_KEY"

# Test Ollama
curl http://localhost:11434/api/tags
```

### Slow responses

- **OpenAI**: Use `gpt-3.5-turbo` instead of `gpt-4`
- **Grok**: Already optimized for speed
- **Local**: Ensure sufficient RAM (8GB+ recommended)

### Rate limits

- **OpenAI**: Tier-based limits, upgrade for higher limits
- **Grok**: Check current limits at https://console.x.ai
- **OpenRouter**: Pay-per-use, no rate limits with credits
- **Local**: No limits!

## Security Best Practices

⚠️ **Never commit API keys!**

```bash
# Add to .gitignore
echo ".env" >> .gitignore
echo "application-dev.yml" >> .gitignore  # if you put keys there

# Use environment variables
export AI_API_KEY="your-key"

# Or use a .env file (with direnv or similar)
echo 'AI_API_KEY=your-key' > .env
```

## Switching Providers

The beauty of Spring AI is you can switch providers without changing code:

```bash
# Monday: Use OpenAI
export AI_API_KEY="sk-openai-key"
export AI_BASE_URL="https://api.openai.com"
mvn spring-boot:run

# Tuesday: Try Grok
export AI_API_KEY="xai-grok-key"
export AI_BASE_URL="https://api.x.ai"
export AI_MODEL="grok-beta"
mvn spring-boot:run

# Wednesday: Go local for privacy
export AI_BASE_URL="http://localhost:11434/v1"
export AI_MODEL="mistral"
mvn spring-boot:run
```

No code changes needed! 🎉

## Recommended Workflow

1. **Development**: Use local Ollama (free, private, fast)
2. **Testing**: Use GPT-3.5 Turbo (cheap, reliable)
3. **Production**: Use GPT-4 Turbo or Grok (best quality)

## Resources

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API](https://platform.openai.com/docs)
- [Grok API](https://docs.x.ai/)
- [OpenRouter](https://openrouter.ai/)
- [Ollama](https://ollama.ai/)
- [GitHub Copilot](https://github.com/features/copilot)

