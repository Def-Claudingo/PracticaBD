package ni.uam.edu.practicabd.Modelos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor@AllArgsConstructor
@Getter@Setter
public class Categoria {
    private Integer id;
    private String nombre;
    private boolean activa;
}
