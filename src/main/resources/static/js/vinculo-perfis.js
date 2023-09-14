$(document).ready(function () {
    if(window.location.href.includes("usuarios/cadastrar") || window.location.href.includes("usuarios/clonar") || window.location.href.includes("usuarios/preEditar")) {

        var usuarioId = $('#usuario-id').val();
        if(window.location.href.includes("clonar")) {
            const arrayUrl = window.location.href.split("/");
            usuarioId = arrayUrl[arrayUrl.length - 1];
        }
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

        var listaUsuariosSelecionados = [];
        $('#form-vinculo-usuarioperfil').submit(function (event) {
            event.preventDefault();

            var listaUsuariosCheck = document.getElementsByClassName("check-vinculo-usuario-perfil");
            for (var i = 0; i < listaUsuariosCheck.length; i++) {
                if (listaUsuariosCheck[i].checked) {
                    listaUsuariosSelecionados.push(listaUsuariosCheck[i].value);
                }
            }

            var modeloCadastroPerfilUsuarioId = {
                perfil: {
                    id: $('#perfil-id').val()
                },
                usuariosPerfilId: listaUsuariosSelecionados
            };

            $.ajax({
                type: "POST",
                url: "http://localhost:8603/perfis/vincular-usuarios-em-lote",
                data: JSON.stringify(modeloCadastroPerfilUsuarioId),
                contentType: "application/json",
                success: function (response) {
                    console.log("Requisição bem sucedida: ", response);
                    window.location.replace("http://localhost:8603/perfis/listar-usuarios-vinculados/" + response);
                },
                error: function (error) {
                    console.log(JSON.stringify(modeloCadastroPerfilUsuarioId));
                    console.log("Erro na requisição: ", error);
                }
            });
        });
    }
});