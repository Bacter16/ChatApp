
package com.exemple.socialapp.domain.validators;


import com.exemple.socialapp.domain.User;

public class UtilizatorValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        //TODO: implement method validate
        validateString(entity.getFirstName());
        validateString(entity.getLastName());
    }

    public static void validateString(String input) throws ValidationException {
        if (input == null || input.isEmpty()) {
            throw new ValidationException("Input string is null or empty.");
        }

        if (Character.isUpperCase(input.charAt(0))) {
            for (int i = 1; i < input.length(); i++) {
                char c = input.charAt(i);
                if (!Character.isLowerCase(c) && !Character.isDigit(c)) {
                    throw new ValidationException("String contains invalid characters.");
                }
            }
        } else {
            throw new ValidationException("String does not start with an uppercase letter.");
        }

        if (input.matches(".*\\d.*")) {
            throw new ValidationException("String contains numbers.");
        }
    }

}

