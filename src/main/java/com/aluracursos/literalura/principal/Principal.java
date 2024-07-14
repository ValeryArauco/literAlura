package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.DatosLibro;
import com.aluracursos.literalura.model.DatosResultadosLibros;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.stream.Collectors;
public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConvierteDatos conversor = new ConvierteDatos();
//    private List<DatosLibro> datosLibros = new ArrayList<>();
    private LibroRepository repositorioLibros;
    private AutorRepository repositorioAutores;
    private List<Libro> libros;
    private List<Autor> autores;
//    private Optional<Libro> libroBuscado;

    public Principal(LibroRepository repositoryBooks, AutorRepository repositoryAuthors) {
        this.repositorioLibros = repositoryBooks;
        this.repositorioAutores = repositoryAuthors;
    }

    public void muestraElMenu() {

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    
                    1 - Buscar libros por título
                    2 - Mostrar libros buscados
                    3 - Mostrar autores buscados
                    4 - Mostrar autores vivos en determinado año
                    5 - Exhibir cantidad de libros en un determinado idioma
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    mostrarLibrosBuscados();
                    break;
                case 3:
                    mostrarAutoresBuscados();
                    break;
                case 4:
                    mostrarAutoresVivosAnio();
                    break;
                case 5:
                    mostrarCantidadDeLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private DatosLibro getDatosLibro() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "%20"));

        DatosResultadosLibros datos = conversor.obtenerDatos(json, DatosResultadosLibros.class);
        return datos.conteo() != 0 ? datos.resultados().get(0) : null;
    }



    private void buscarLibro() {
        DatosLibro datos = getDatosLibro();
        if (datos != null) {
            try {
                Autor autor = repositorioAutores.findByNombre(datos.autores().get(0).nombre())
                        .orElseGet(() -> new Autor(datos.autores().get(0)));

                Libro libro = new Libro(datos);
                libro.setAutor(autor);

                // Agregar el libro al autor solo si no existe ya en la lista de libros
                if (autor.getLibros() == null) {
                    autor.setLibros(new ArrayList<>());
                }

                if (!autor.getLibros().contains(libro)) {
                    autor.getLibros().add(libro);
                }

                repositorioAutores.save(autor);
                repositorioLibros.save(libro);

                System.out.println(libro);
            } catch (DataIntegrityViolationException e) {}
        } else {
            System.out.println("No se ha encontrado el libro");
        }
    }



    private void mostrarLibrosBuscados() {
        libros = repositorioLibros.findAll();
        libros.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(System.out::println);
    }

    private void mostrarAutoresBuscados() {
        autores = repositorioAutores.findAll();
        autores.stream()
                .sorted(Comparator.comparing(Autor::getNombre))
                .forEach(System.out::println);
    }


    private void mostrarAutoresVivosAnio() {
        System.out.println("Escribe el año");
        Integer año = Integer.valueOf(teclado.nextLine());
        List<Autor> autoresVivos = repositorioAutores.findByYear(año);
        autoresVivos.forEach(a ->
                System.out.println(a.toString()));

    }


    private void mostrarCantidadDeLibrosPorIdioma() {
        System.out.println("Escribe el idioma");
        var idioma = teclado.nextLine();
        List<Libro> libroList = repositorioLibros.findByIdioma(idioma);
        System.out.println(libroList);

    }


}
