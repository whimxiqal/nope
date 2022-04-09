# Contributing to Nope

Thank you for contributing to Nope!
Below are some things to keep in mind.

## Git

Fork the project and submit code with pull requests with preferably only one commit.
If you have more commits, they will be squashed down before merging anyway.

## Style

Some important information about the style

> Note: this project uses Checkstyle to lint the code. 
> Download a suitable plugin to your IDE (Checkstyle-IDEA for IntelliJ)
> to ensure that you pass the checks before you submit your request.

### Generics

This project uses lots of generics because of the nature of variable-type settings.

When defining a class with some primary type of value held, 
use `T` to represent the data type. When defining a special type of value stored,
use `V`. You can see that with `SettingKey`s, `T` and `V` are used for the two
generic types in this fashion.

When creating static methods that require generics, refrain from using the same letter
convention. Instead, use the last letters of the alphabet (`X`, `Y`, `Z`) 
to hint that they are in place of the standard letters.
For example, builders of components of `SettingKey`s use type `T`
but the static constructors use `X`.