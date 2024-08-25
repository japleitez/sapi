package com.peecko.api.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameValidatorTest {

    @Test
    void isValidName() {

        assertTrue(NameValidator.isValid("John Doe"));
        assertTrue(NameValidator.isValid("Jane Marie Smith"));
        assertTrue(NameValidator.isValid("Juan Carlos Fernández"));
        assertTrue(NameValidator.isValid("Ana María Rodríguez de Santos"));
        assertTrue(NameValidator.isValid("Jürgen Müller"));
        assertTrue(NameValidator.isValid("Marie-Odile Dupont"));
        assertTrue(NameValidator.isValid("O'Conner"));
        assertTrue(NameValidator.isValid("Mário José da Silva"));
        assertTrue(NameValidator.isValid("José María López"));
        assertTrue(NameValidator.isValid("XXXX"));

        //pt
        assertTrue(NameValidator.isValid("José Brasão"));

        //fr
        assertTrue(NameValidator.isValid("Léonard Gaultier"));
        assertTrue(NameValidator.isValid("Nicolas de Largillière"));
        assertTrue(NameValidator.isValid("Jean-Baptiste-Siméon Chardin"));
        assertTrue(NameValidator.isValid("Louis Albert Guislain Bacler d'Albe"));
        assertTrue(NameValidator.isValid("Ignace François Bonhomme"));
        assertTrue(NameValidator.isValid("Jean-François Millet"));

        //de
        assertTrue(NameValidator.isValid("Holger Göpfert"));
        assertTrue(NameValidator.isValid("Marius Müller-Westernhagen"));
        assertTrue(NameValidator.isValid("Schäfer Heinrich"));
        assertTrue(NameValidator.isValid("Daniel Küblböck"));
        assertTrue(NameValidator.isValid("Micaela Schäfer"));
        assertTrue(NameValidator.isValid("Ruby O. Fee"));
        assertTrue(NameValidator.isValid("Astrid M. Fünderich"));

    }

    @Test
    void isNotValidName() {
        assertTrue(NameValidator.isNotValid("X Æ A-12"));
        assertTrue(NameValidator.isNotValid("12345"));
        assertTrue(NameValidator.isNotValid("John@Doe"));
        assertTrue(NameValidator.isNotValid(""));

        assertTrue(NameValidator.isNotValid("José! Brasão"));
        assertTrue(NameValidator.isNotValid("José@ Brasão"));
        assertTrue(NameValidator.isNotValid("José# Brasão"));
        assertTrue(NameValidator.isNotValid("José$ Brasão"));
        assertTrue(NameValidator.isNotValid("José% Brasão"));
        assertTrue(NameValidator.isNotValid("José^ Brasão"));
        assertTrue(NameValidator.isNotValid("José& Brasão"));
        assertTrue(NameValidator.isNotValid("José* Brasão"));
        assertTrue(NameValidator.isNotValid("José( Brasão"));
        assertTrue(NameValidator.isNotValid("José) Brasão"));
        assertTrue(NameValidator.isNotValid("José[ Brasão"));
        assertTrue(NameValidator.isNotValid("José] Brasão"));
        assertTrue(NameValidator.isNotValid("José+ Brasão"));
        assertTrue(NameValidator.isNotValid("José= Brasão"));
        assertTrue(NameValidator.isNotValid("José| Brasão"));
        assertTrue(NameValidator.isNotValid("José\\ Brasão"));
        assertTrue(NameValidator.isNotValid("José/ Brasão"));
        assertTrue(NameValidator.isNotValid("José~ Brasão"));
        assertTrue(NameValidator.isNotValid("José` Brasão"));
        assertTrue(NameValidator.isNotValid("José0 Brasão"));
        assertTrue(NameValidator.isNotValid("José1 Brasão"));
        assertTrue(NameValidator.isNotValid("José2 Brasão"));
        assertTrue(NameValidator.isNotValid("José3 Brasão"));
        assertTrue(NameValidator.isNotValid("José4 Brasão"));
        assertTrue(NameValidator.isNotValid("José5 Brasão"));
        assertTrue(NameValidator.isNotValid("José6 Brasão"));
        assertTrue(NameValidator.isNotValid("José7 Brasão"));
        assertTrue(NameValidator.isNotValid("José8 Brasão"));
        assertTrue(NameValidator.isNotValid("José9 Brasão"));

    }

}