---
name: Feature request
about: Suggest an idea for this project
title: '[FEATURE] '
labels: ['enhancement']
assignees: ['alex-inqwise']

---

## Is your feature request related to a problem? Please describe.
A clear and concise description of what the problem is. Ex. I'm always frustrated when [...]

## Describe the solution you'd like
A clear and concise description of what you want to happen.

## Describe alternatives you've considered
A clear and concise description of any alternative solutions or features you've considered.

## Use Case
Describe the specific use case for this feature:
- What are you trying to accomplish?
- How would this feature help you or others?
- How often would you use this feature?

## Proposed API
If you have ideas about how the API should look:

```java
// Example of how you'd like to use this feature
Differentiator differ = Differentiator.builder()
    .withNewFeature(someConfiguration)
    .build();
```

## RFC 6902 Compliance
If this feature affects JSON Patch operations:
- [ ] This feature maintains RFC 6902 compliance
- [ ] This feature extends RFC 6902 in a compatible way
- [ ] This feature requires breaking RFC 6902 compliance (please justify)

## Implementation Considerations
- Does this require new dependencies?
- Does this affect performance?
- Does this maintain backward compatibility?
- Are there any security implications?

## Additional context
Add any other context or screenshots about the feature request here.