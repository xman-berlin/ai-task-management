# AI Task Prioritization Agent - Implementation Guide

## Overview
The Task Prioritization Agent uses OpenAI's GPT-4 to intelligently suggest task priorities based on title, description, and due date.

## Setup

### 1. Get OpenAI API Key
1. Go to https://platform.openai.com/api-keys
2. Create a new API key
3. Copy the key (starts with `sk-...`)

### 2. Configure the Application

**Option A: Environment Variable (Recommended)**
```bash
export OPENAI_API_KEY="sk-your-actual-key-here"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Option B: Update application-dev.yml**
```yaml
spring:
  ai:
    openai:
      api-key: sk-your-actual-key-here  # DO NOT commit this!
```

### 3. Run the Application
```bash
cd /Users/xman/projects/test/spring-boot-4-test
export OPENAI_API_KEY="sk-your-key"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## How It Works

### AI Prompt Engineering
The agent uses a carefully crafted prompt that considers:
- **Urgency**: How soon is the due date?
- **Impact**: Is it critical or foundational?
- **Complexity**: Quick task or major project?
- **Dependencies**: Does it block other work?

### Fallback Strategy
If the AI call fails (network, quota, etc.), the system falls back to a heuristic:
- **HIGH**: Due in <48 hours
- **LOW**: Long description (>500 chars, likely research)
- **MEDIUM**: Everything else

### Architecture
```
User clicks "AI Suggest"
    ↓
Frontend JS → /api/ai/prioritize
    ↓
AiService.prioritize(TaskDto)
    ↓
Spring AI ChatClient → OpenAI GPT-4
    ↓
Parse JSON response
    ↓
Update UI: Priority + Rationale
```

## Using the Feature

### In the UI
1. Open the task creation/edit modal
2. Fill in **Title** (required)
3. Optionally add **Description** and **Due Date** for better suggestions
4. Click **"AI Suggest"** button next to Priority field
5. Wait 1-3 seconds for AI response
6. Priority field updates automatically
7. See the AI's rationale below the priority dropdown

### Via API
```bash
curl -X POST http://localhost:18080/api/ai/prioritize \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Deploy new feature to production",
    "description": "Critical bug fix affecting all users",
    "dueDate": "2026-01-10T17:00:00"
  }'
```

Response:
```json
{
  "priority": "HIGH",
  "rationale": "Critical production issue with imminent deadline requires immediate attention"
}
```

## Cost Considerations

### OpenAI Pricing (as of Jan 2026)
- **GPT-4 Turbo**: ~$0.01 per 1K tokens
- **Average cost per suggestion**: ~$0.002-0.005 (2-5 cents per 100 suggestions)

### Optimization Tips
1. **Caching**: Store suggestions for similar tasks (future enhancement)
2. **Batch processing**: Suggest priorities for multiple tasks at once
3. **Fallback first**: Use heuristics for simple cases, AI for complex ones
4. **Rate limiting**: Prevent abuse with request limits

## Testing

### Unit Test
```java
@Test
void testPrioritizationWithUrgentTask() {
    TaskDto dto = new TaskDto(
        null,
        "Critical production bug",
        "System down, users affected",
        Priority.MEDIUM,
        Status.TODO,
        LocalDateTime.now().plusHours(6)
    );
    
    PrioritySuggestion result = aiService.prioritize(dto);
    
    assertThat(result.priority()).isEqualTo(Priority.HIGH);
    assertThat(result.rationale()).contains("urgent");
}
```

### Manual Testing
1. **Test with API key**: Set real OpenAI key, create task with description
2. **Test fallback**: Remove API key, verify heuristic works
3. **Test edge cases**: Empty description, null due date, very long text

## Monitoring

### Logs to Watch
```bash
# AI response logging
2026-01-09 INFO  AiService - AI prioritization response: {"priority":"HIGH","rationale":"..."}

# Fallback triggered
2026-01-09 WARN  AiService - AI prioritization failed, falling back to heuristic: ...

# Spring AI debug
2026-01-09 DEBUG org.springframework.ai - Sending request to OpenAI...
```

### Metrics to Track (future)
- AI call success rate
- Average response time
- Fallback frequency
- Cost per day/week

## Next Steps

### Planned Enhancements
1. **Task Decomposition Agent**: Break large tasks into subtasks
2. **Deadline Prediction Agent**: Suggest realistic deadlines based on complexity
3. **Smart Notifications Agent**: Determine best time to remind users
4. **Context-Aware Suggestions**: Learn from user's historical priority decisions

### Improvements
- Add JSON schema validation for responses
- Implement response caching with Redis
- Add user feedback loop ("Was this suggestion helpful?")
- Support multiple AI providers (Anthropic, Gemini, local models)

## Troubleshooting

### "AI unavailable" in rationale
- Check OpenAI API key is set correctly
- Verify network connectivity to OpenAI
- Check OpenAI status: https://status.openai.com
- Review logs for specific error message

### Incorrect priorities suggested
- Improve prompt engineering
- Add more context in task description
- Provide better examples in the prompt
- Consider fine-tuning a custom model

### Slow responses (>5 seconds)
- Check OpenAI API latency
- Consider switching to `gpt-3.5-turbo` for faster responses
- Implement request timeout and show loading state
- Add response streaming for better UX

## Security Notes

⚠️ **Never commit API keys to Git!**
- Use environment variables
- Add `application-dev.yml` to `.gitignore` if you edit it with keys
- Rotate keys regularly
- Use separate keys for dev/prod

## Resources

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API Reference](https://platform.openai.com/docs/api-reference)
- [Prompt Engineering Guide](https://platform.openai.com/docs/guides/prompt-engineering)

