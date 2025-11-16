package aed;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;

// Aula vacía,
// Aula llena, con un alumno, examen de darkWeb todas incorrectas, 
// copiarse para adelante y a los costados, sin personas para copiar, 
// poner a prueba si el heap está bien, o sea, bien ordenado por las notas, tipo un test de estrés


//! Por algun motivo no nos deja correr los tests desde arriba a la derecha en visual pero si tocando a la izquierada del nombre de la clase
class TestsPropios {
  Edr edr;

  @Test
  void aula_vacia() {
    edr = new Edr(5, 0, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
    double[] notas_esperadas = new double[] {};
    double[] notas_obtenidas = edr.notas();
    assertTrue(Arrays.equals(notas_esperadas, notas_obtenidas));
    edr.consultarDarkWeb(0, new int[] {});
    notas_obtenidas = edr.notas();
    assertTrue(Arrays.equals(notas_esperadas, notas_obtenidas));
    int[] copiones = edr.chequearCopias();
    int[] copiones_esperados = new int[] {};
    assertTrue(Arrays.equals(copiones_esperados, copiones));
    NotaFinal[] notas_finales = edr.corregir();
    NotaFinal[] notas_finales_esperadas = new NotaFinal[] {};
    assertTrue(Arrays.equals(notas_finales_esperadas, notas_finales));
  }

  @Test
  void aula_llena() {
    edr = new Edr(5, 15, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
    int[] examenDW = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    edr.consultarDarkWeb(6, examenDW);
    int[] copiones = edr.chequearCopias();
    int[] copiones_esperados = new int[] { 0, 1, 2, 3, 4, 5 };
    assertTrue(Arrays.equals(copiones_esperados, copiones));

    for (int alumno = 7; alumno < 15; alumno++) {
      for (int pregunta = 0; pregunta < alumno - 6; pregunta++) {
        edr.resolver(alumno, pregunta, pregunta);
      }
    }

    for (int alumno = 0; alumno < 15; alumno++) {
      edr.entregar(alumno);
    }

    double[] notas_esperadas = new double[] { 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 0, 10, 20, 30, 40, 50, 60, 70,
        80 };
    double[] notas_obtenidas = edr.notas();
    assertTrue(Arrays.equals(notas_esperadas, notas_obtenidas));

    NotaFinal[] notas_finales = edr.corregir();
    NotaFinal[] notas_finales_esperadas = new NotaFinal[] {
        new NotaFinal(80.0, 14),
        new NotaFinal(70.0, 13),
        new NotaFinal(60.0, 12),
        new NotaFinal(50.0, 11),
        new NotaFinal(40.0, 10),
        new NotaFinal(30.0, 9),
        new NotaFinal(20.0, 8),
        new NotaFinal(10.0, 7),
        new NotaFinal(0.0, 6),
    };
    assertTrue(Arrays.equals(notas_finales_esperadas, notas_finales));
  }

  @Test
  void un_solo_alumno() {
    edr = new Edr(5, 1, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
    edr.copiarse(0);

    double[] notas_esperadas = new double[] { 0 };
    double[] notas_obtenidas = edr.notas();
    assertTrue(Arrays.equals(notas_esperadas, notas_obtenidas));

    int[] copiones = edr.chequearCopias();
    int[] copiones_esperados = new int[] {};
    assertTrue(Arrays.equals(copiones_esperados, copiones));

    for (int pregunta = 0; pregunta < 10; pregunta++) {
      edr.resolver(0, pregunta, pregunta);
    }
    notas_esperadas = new double[] { 100.00 };
    notas_obtenidas = edr.notas();
    assertTrue(Arrays.equals(notas_esperadas, notas_obtenidas));

    edr.entregar(0);
    NotaFinal[] notas_finales = edr.corregir();
    NotaFinal[] notas_finales_esperadas = new NotaFinal[] {
        new NotaFinal(100.0, 0),
    };
    assertTrue(Arrays.equals(notas_finales_esperadas, notas_finales));
  }

  @Test
  void un_solo_alumno_darkweb() {
    edr = new Edr(5, 1, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
    edr.copiarse(0);

    double[] notas_esperadas = new double[] { 0 };
    double[] notas_obtenidas = edr.notas();
    assertTrue(Arrays.equals(notas_esperadas, notas_obtenidas));

    int[] copiones = edr.chequearCopias();
    int[] copiones_esperados = new int[] {};
    assertTrue(Arrays.equals(copiones_esperados, copiones));

    int[] examenDW = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    edr.consultarDarkWeb(1, examenDW);
    notas_esperadas = new double[] { 100.00 };
    notas_obtenidas = edr.notas();
    assertTrue(Arrays.equals(notas_esperadas, notas_obtenidas));
  }

  @Test
  void se_copia_de_adelante() {
    edr = new Edr(5, 8, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });

    for (int pregunta = 0; pregunta < 5; pregunta++) {
      edr.resolver(4, pregunta, pregunta);
    }

    for (int pregunta = 0; pregunta < 5; pregunta++) {
      edr.copiarse(1); // El 1 tiene adelante al 4
    }

    for (int pregunta = 0; pregunta < 5; pregunta++) {
      edr.resolver(7, pregunta, pregunta);
    }

    for (int pregunta = 0; pregunta < 5; pregunta++) {
      edr.copiarse(4); // El 4 tiene adelante al 7
    }

    double[] notas_esperadas = new double[] { 0.0, 50.0, 0.0, 0.0, 50.0, 0.0, 0.0, 50.0 };
    double[] notas_obtenidas = edr.notas();
    assertTrue(Arrays.equals(notas_esperadas, notas_obtenidas));

    int[] copiones = edr.chequearCopias();
    int[] copiones_esperados = new int[] { 1, 4, 7 };
    assertTrue(Arrays.equals(copiones_esperados, copiones));
  }

  @Test
  void nadie_entrego_todavia() {
    edr = new Edr(5, 3, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });

    for (int alumno = 0; alumno < 3; alumno++) {
      for (int pregunta = 0; pregunta < 10; pregunta++) {
        edr.resolver(alumno, pregunta, pregunta);
      }
    }

    double[] notas_esperadas = new double[] { 100.0, 100.0, 100.0 };
    double[] notas_obtenidas = edr.notas();
    assertTrue(Arrays.equals(notas_esperadas, notas_obtenidas));

    NotaFinal[] notas_finales = edr.corregir();
    NotaFinal[] notas_finales_esperadas = null;
    assertTrue(Arrays.equals(notas_finales_esperadas, notas_finales));
  }

  @Test
  void darkWeb_incorrecto() {
    edr = new Edr(5, 2, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });

    int[] examenDW = new int[] { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
    edr.consultarDarkWeb(2, examenDW);

    double[] notas_esperadas = new double[] { 0.0, 0.0 };
    double[] notas_obtenidas = edr.notas();
    assertTrue(Arrays.equals(notas_esperadas, notas_obtenidas));
  }

  @Test
  void aula_masiva_dark_web() {
    edr = new Edr(1000, 10000, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });

    edr.consultarDarkWeb(5000, new int[] { 0, 0, 2, 3, 7, 5, 6, 7, 8, 8 });

    int[] copiones = edr.chequearCopias();
    int[] copiones_esperados = new int[5000];

    for (int i = 0; i < 5000; i++) {
      copiones_esperados[i] = i;
    }

    assertTrue(Arrays.equals(copiones_esperados, copiones));
  }

  @Test
  void aula_masiva_se_copian() {
    edr = new Edr(1000, 10000, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });

    for (int pregunta = 0; pregunta < 10; pregunta++) {
      edr.resolver(9999, pregunta, pregunta);
    }

    // Se van copiando del de al lado
    for (int estudiante = 9998; estudiante > 5000; estudiante--) {
      for (int pregunta = 0; pregunta < 10; pregunta++) {
        edr.copiarse(estudiante);
      }
    }

    int[] copiones = edr.chequearCopias();
    int[] copiones_esperados = new int[4999];
    for (int i = 0; i < 4999; i++) {
      copiones_esperados[i] = 5001 + i;

    }
    assertTrue(Arrays.equals(copiones_esperados, copiones));
  }
}