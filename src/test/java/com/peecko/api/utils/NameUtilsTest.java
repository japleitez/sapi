package com.peecko.api.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameUtilsTest {

    @Test
    public void validTest() {
        //pt
        assertTrue(NameUtils.isValid("José Brasão"));

        //fr
        assertTrue(NameUtils.isValid("Léonard Gaultier"));
        assertTrue(NameUtils.isValid("Nicolas de Largillière"));
        assertTrue(NameUtils.isValid("Jean-Baptiste-Siméon Chardin"));
        assertTrue(NameUtils.isValid("Louis Albert Guislain Bacler d'Albe"));
        assertTrue(NameUtils.isValid("Ignace François Bonhomme"));
        assertTrue(NameUtils.isValid("Jean-François Millet"));

        //de
        assertTrue(NameUtils.isValid("Holger Göpfert"));
        assertTrue(NameUtils.isValid("Marius Müller-Westernhagen"));
        assertTrue(NameUtils.isValid("Schäfer Heinrich"));
        assertTrue(NameUtils.isValid("Daniel Küblböck"));
        assertTrue(NameUtils.isValid("Micaela Schäfer"));
        assertTrue(NameUtils.isValid("Ruby O. Fee"));
        assertTrue(NameUtils.isValid("Astrid M. Fünderich"));
    }

    @Test
    public void invalidTest() {
        assertFalse(NameUtils.isValid("José! Brasão"));
        assertFalse(NameUtils.isValid("José@ Brasão"));
        assertFalse(NameUtils.isValid("José# Brasão"));
        assertFalse(NameUtils.isValid("José$ Brasão"));
        assertFalse(NameUtils.isValid("José% Brasão"));
        assertFalse(NameUtils.isValid("José^ Brasão"));
        assertFalse(NameUtils.isValid("José& Brasão"));
        assertFalse(NameUtils.isValid("José* Brasão"));
        assertFalse(NameUtils.isValid("José( Brasão"));
        assertFalse(NameUtils.isValid("José) Brasão"));
        assertFalse(NameUtils.isValid("José[ Brasão"));
        assertFalse(NameUtils.isValid("José] Brasão"));
        assertFalse(NameUtils.isValid("José+ Brasão"));
        assertFalse(NameUtils.isValid("José= Brasão"));
        assertFalse(NameUtils.isValid("José| Brasão"));
        assertFalse(NameUtils.isValid("José\\ Brasão"));
        assertFalse(NameUtils.isValid("José/ Brasão"));
        assertFalse(NameUtils.isValid("José~ Brasão"));
        assertFalse(NameUtils.isValid("José` Brasão"));
        assertFalse(NameUtils.isValid("José0 Brasão"));
        assertFalse(NameUtils.isValid("José1 Brasão"));
        assertFalse(NameUtils.isValid("José2 Brasão"));
        assertFalse(NameUtils.isValid("José3 Brasão"));
        assertFalse(NameUtils.isValid("José4 Brasão"));
        assertFalse(NameUtils.isValid("José5 Brasão"));
        assertFalse(NameUtils.isValid("José6 Brasão"));
        assertFalse(NameUtils.isValid("José7 Brasão"));
        assertFalse(NameUtils.isValid("José8 Brasão"));
        assertFalse(NameUtils.isValid("José9 Brasão"));
    }

    @Test
    public void countTest() {
        assertEquals(2, NameUtils.split("José Brasão").size());
        assertEquals(3, NameUtils.split(" Nicolas de  Largillière  ").size());
    }

    @Test
    public void trimTest() {
        assertEquals("José Brasão de la O.", NameUtils.trim("José Brasão de  la  O."));
        assertEquals("Nicolas de Largillière", NameUtils.trim(" Nicolas de  Largillière  "));
    }

    @Test
    public void camelTest() {
        assertEquals("José Brasão de la O.", NameUtils.camel("JOSé BRASãO DE  LA  o."));
        assertEquals("Nicolas de Largillière", NameUtils.camel(" NICOLAS DE  LARgillière  "));
    }

}
