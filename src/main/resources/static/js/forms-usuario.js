// TODO Fazer o submit correto das informações baseado nos diferentes formulários
$(document).ready(function () {
    const urlAtual = window.location.href;
    if (urlAtual.includes("usuarios/cadastrar") || urlAtual.includes("usuarios/clonar") || urlAtual.includes("usuarios/preEditar")) {

        var usuarioId = $('#usuario-id').val();
        if (urlAtual.includes("clonar")) {
            const arrayUrl = urlAtual.split("/");
            usuarioId = arrayUrl[arrayUrl.length - 1];
        }

        // Requisição padrão para toda página de usuários com CheckBox
        $.ajax({
            type: "GET",
            url: '/usuarios/get-todos-perfis',
            contentType: "application/json",
            success: function (data) {
                console.log(data);
                // Divida a lista ao meio
                const middleIndex = Math.ceil(data.length / 2);
                const coluna1 = data.slice(0, middleIndex);
                const coluna2 = data.slice(middleIndex);

                // Preencha as colunas com os nomes dos usuários
                const coluna1Element = $('#coluna-1');
                const coluna2Element = $('#coluna-2');

                coluna1.forEach(function (perfil) {
                    const li = $('<li>').html(`<input type="checkbox" class="form-check-input check-vinculo-usuario-perfil" id="${perfil.id}" value="${perfil.id}"/> <label class="form-check-label" for="${perfil.id}">${perfil.nome}</label>`);
                    coluna1Element.append(li);
                });

                coluna2.forEach(function (perfil) {
                    const li = $('<li>').html(`<input type="checkbox" class="form-check-input check-vinculo-usuario-perfil" id="${perfil.id}" value="${perfil.id}"/> <label class="form-check-label" for="${perfil.id}">${perfil.nome}</label>`);
                    coluna2Element.append(li);
                });

                $.ajax({
                    type: "GET",
                    url: "/usuarios/get-perfis-vinculados/" + usuarioId,
                    success: function (response) {
                        var usuariosVinculados = response;
                        var listaUsuariosCheck = document.getElementsByClassName("check-vinculo-usuario-perfil");
                        for (var i = 0; i< listaUsuariosCheck.length; i++) {
                            if (usuariosVinculados.includes(parseInt(listaUsuariosCheck[i].value))) {
                                listaUsuariosCheck[i].checked = true;
                            }
                        }
                    },
                    error: function (error) {
                        console.error("Erro ao carregar perfis:", error);
                    }
                });
            },
            error: function (error) {
                console.error('Erro ao buscar a lista de usuários:', error);
            }
        });

        var listaPerfisSelecionados = [];

        // Formulário de cadastro
        $('#form-usuario').submit(function (event) {
            event.preventDefault();

            var listaPerfisCheck = document.getElementsByClassName("check-vinculo-usuario-perfil");
            for (var i = 0; i < listaPerfisCheck.length; i++) {
                if (listaPerfisCheck[i].checked) {
                    listaPerfisSelecionados.push(listaPerfisCheck[i].value);
                }
            }

            document.getElementById("perfisUsuario").value = listaPerfisSelecionados;
            listaPerfisSelecionados = [];
            document.getElementById("form-usuario").submit();
        });
    }
});