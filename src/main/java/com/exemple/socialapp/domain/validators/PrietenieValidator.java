package com.exemple.socialapp.domain.validators;


import com.exemple.socialapp.domain.Friendship;

public class PrietenieValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        UtilizatorValidator validator = new UtilizatorValidator();
//        Utilizator u1 = entity.getU1();
//        Utilizator u2 = entity.getU2();
//        validator.validate(u1);
//        validator.validate(u2);
        if(entity.getU1() == entity.getU2())
            throw new ValidationException("You can't be your own friend!");
    }
}
