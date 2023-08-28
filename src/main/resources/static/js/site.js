setTraducaoDataTable('#table-permissoes')
setTraducaoDataTable('#table-perfis')
setTraducaoDataTable('#table-usuarios')
$('.btn-total-contatos').click(function () {
    var usuarioId = $(this).attr('usuario-id');
    var urlCompleta = '/Usuario/ListarContatosPorUsuario/' + usuarioId;
    console.log(urlCompleta);
    $.ajax({
        type: 'GET',
        url: urlCompleta,
        success: function (result) {
            $("#corpoModal").html(result)
            setTraducaoDataTable('#table-contatos-usuario');
        }
    })
})

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

$('button[id*="btn_"]').click(function() {
    url = "http://localhost:8603/" + $(this).attr('id').split("_")[1];
});

$('#confirmarExclusao').click(function() {
    document.location.href = url;
});

$(function() {
    $('[data-toggle="popover"]').popover();
});

$(document).ready(function(){
    $(".navbar-toggle").click(function(){
        $(".sidebar").toggleClass("sidebar-open");
    })
});

var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))

var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
    return new bootstrap.Popover(popoverTriggerEl)
})

var popover = new bootstrap.Popover(document.querySelector('.popover-dismiss'), {
    trigger: 'focus'
})