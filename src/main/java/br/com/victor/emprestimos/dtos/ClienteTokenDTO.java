package br.com.victor.emprestimos.dtos;

import br.com.victor.emprestimos.domain.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteTokenDTO {

    private String name;
    private String cpf;
    private String senha;


    public static String converte(Cliente cliente){
        ClienteTokenDTO dto = new ClienteTokenDTO();
        dto.senha = cliente.getSenha().replace(cliente.getSenha(),"****************************");
        dto.cpf = cliente.getCpf();
        dto.name = cliente.getNome();
        return dto.toString();
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }
}
