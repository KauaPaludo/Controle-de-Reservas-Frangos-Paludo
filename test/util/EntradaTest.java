package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntradaTest {

    @Test
    void unidadesAceitaInteiroEMeio() {
        assertEquals(2.0, Entrada.unidades("2"));
        assertEquals(1.5, Entrada.unidades("1,5"));
        assertEquals(1.5, Entrada.unidades("1.5"));
    }

    @Test
    void unidadesRecusaValoresInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> Entrada.unidades("1,3"));
        assertThrows(IllegalArgumentException.class, () -> Entrada.unidades("0"));
        assertThrows(IllegalArgumentException.class, () -> Entrada.unidades("-1"));
        assertThrows(IllegalArgumentException.class, () -> Entrada.unidades("abc"));
    }

    @Test
    void pesoAceitaVirgula() {
        assertEquals(1.5, Entrada.peso("1,5"));
        assertThrows(IllegalArgumentException.class, () -> Entrada.peso("0"));
    }

    @Test
    void telefoneEOpcionalMasValidado() {
        assertEquals("", Entrada.telefone(""));
        assertEquals("54999998888", Entrada.telefone("(54) 99999-8888"));
        assertThrows(IllegalArgumentException.class, () -> Entrada.telefone("123"));
    }
}