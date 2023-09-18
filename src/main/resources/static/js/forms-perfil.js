// TODO Fazer o submit correto das informações baseado nos diferentes formulários
$(document).ready(function () {
    const urlAtual = window.location.href;

    if(urlAtual.includes("vincular-usuarios-em-lote")) {
            $.ajax({
                type: "GET",
                url: '/perfis/get-usuarios',
                contentType: "application/json",
                success: function (data) {
                    // Divida a lista ao meio
                    const middleIndex = Math.ceil(data.length / 2);
                    const coluna1 = data.slice(0, middleIndex);
                    const coluna2 = data.slice(middleIndex);

                    // Preencha as colunas com os nomes dos usuários
                    const coluna1Element = $('#coluna-1');
                    const coluna2Element = $('#coluna-2');

                    coluna1.forEach(function (usuario) {
                        const li = $('<li>').html(`<input type="checkbox" class="form-check-input check-vinculo-usuario-perfil" id="${usuario.id}" value="${usuario.id}"/> <label class="form-check-label" for="${usuario.id}">${usuario.nomeUser}</label>`);
                        coluna1Element.append(li);
                    });

                    coluna2.forEach(function (usuario) {
                        const li = $('<li>').html(`<input type="checkbox" class="form-check-input check-vinculo-usuario-perfil" id="${usuario.id}" value="${usuario.id}"/> <label class="form-check-label" for="${usuario.id}">${usuario.nomeUser}</label>`);
                        coluna2Element.append(li);
                    });

                    var perfilId = $('#perfil-id').val()
                    $.ajax({
                        type: "GET",
                        url: "/perfis/get-usuarios-vinculados/" + perfilId,
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

    if (urlAtual.includes("perfis/cadastrar") || urlAtual.includes("perfis/clonar") || urlAtual.includes("perfis/preEditar")) {

        var perfilId = $('#perfil-id').val();
        if (urlAtual.includes("clonar")) {
            const arrayUrl = urlAtual.split("/");
            perfilId = arrayUrl[arrayUrl.length - 1];
        }

        // Requisição padrão para toda página de usuários com CheckBox
        $.ajax({
            type: "GET",
            url: '/perfis/get-todas-permissoes',
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

                coluna1.forEach(function (permissao) {
                    const li = $('<li>').html(`<input type="checkbox" class="form-check-input check-vinculo-perfil-permissao" id="${permissao.id}" value="${permissao.id}"/> <label class="form-check-label" for="${permissao.id}">${permissao.nome}</label>`);
                    coluna1Element.append(li);
                });

                coluna2.forEach(function (permissao) {
                    const li = $('<li>').html(`<input type="checkbox" class="form-check-input check-vinculo-perfil-permissao" id="${permissao.id}" value="${permissao.id}"/> <label class="form-check-label" for="${permissao.id}">${permissao.nome}</label>`);
                    coluna2Element.append(li);
                });

                $.ajax({
                    type: "GET",
                    url: "/perfis/get-permissoes-vinculadas/" + perfilId,
                    success: function (response) {
                        var permissoesVinculadas = response;
                        var listaPermissoesCheck = document.getElementsByClassName("check-vinculo-perfil-permissao");
                        for (var i = 0; i< listaPermissoesCheck.length; i++) {
                            if (permissoesVinculadas.includes(parseInt(listaPermissoesCheck[i].value))) {
                                listaPermissoesCheck[i].checked = true;
                            }
                        }
                    },
                    error: function (error) {
                        console.error("Erro ao carregar permissões vinculadas:", error);
                    }
                });
            },
            error: function (error) {
                console.error('Erro ao buscar a lista de permissões gerais:', error);
            }
        });

        var listaPermissoesSelecionadas = [];

        // Formulário de cadastro
        $('#form-perfil').submit(function (event) {
            event.preventDefault();

            var listaPerfisCheck = document.getElementsByClassName("check-vinculo-perfil-permissao");
            for (var i = 0; i < listaPerfisCheck.length; i++) {
                if (listaPerfisCheck[i].checked) {
                    listaPermissoesSelecionadas.push(listaPerfisCheck[i].value);
                }
            }

            document.getElementById("permissoesPerfil").value = listaPermissoesSelecionadas;
            listaPerfisSelecionados = [];
            document.getElementById("form-perfil").submit();
        });
    }
});