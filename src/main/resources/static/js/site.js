setTraducaoDataTable('#table-permissoes')
setTraducaoDataTable('#table-perfis')
setTraducaoDataTable('#table-usuarios')

function setTraducaoDataTable(id) {
    $(id).DataTable({
        "ordering": true,
        "paging": true,
        "searching": true,
        "oLanguage": {
            "sEmptyTable": "Nenhum registro encontrado na tabela",
            "sInfo": "Mostrar _START_ até _END_ de _TOTAL_ registros",
            "sInfoEmpty": "Mostrar 0 até 0 de 0 Registros",
            "sInfoFiltered": "(Filtrar de _MAX_ total registros)",
            "sInfoPostFix": "",
            "sInfoThousands": ".",
            "sLengthMenu": "Mostrar _MENU_ registros por pagina",
            "sLoadingRecords": "Carregando...",
            "sProcessing": "Processando...",
            "sZeroRecords": "Nenhum registro encontrado",
            "sSearch": "Pesquisar",
            "oPaginate": {
                "sNext": "Proximo",
                "sPrevious": "Anterior",
                "sFirst": "Primeiro",
                "sLast": "Ultimo"
            },
            "oAria": {
                "sSortAscending": ": Ordenar colunas de forma ascendente",
                "sSortDescending": ": Ordenar colunas de forma descendente"
            }
        }
    });
}

var url = '';

function deletePerfil(obj) {
    url = "http://localhost:8603/" + $(obj).attr('id').split("_")[1];
}

$('#confirmarExclusao').click(function() {
    document.location.href = url;
});

$(".check-exibicao").on("click", function(event) {
    event.preventDefault();
});

$(document).ready(function () {

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
});