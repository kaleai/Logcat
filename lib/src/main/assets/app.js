var isDebug;
var currentLev = 'VERBOSE';

$( document ).ready(function() {
    isDebug = false;
    loadLogList(); // first load
    
    document.log_lev_radio.onclick = function(){
        var lev = document.log_lev_radio.rate.value;
        console.log(lev);
        currentLev = lev;
        loadLogListByLev();
    }
});

function loadLogList(){
    if (!isDebug) {
        fetchLogList();
    }else{
        fetchTestApi();
    }
}

function loadLogListByLev(){
    $("#log-table").DataTable().destroy();
   if (!isDebug) {
        fetchLogListByLev(currentLev);
    }else{
        fetchTestApi();
    }
}

function fetchLogList(){
    $.ajax({
        url: "getLogList",
        type: "GET",
        dataType: 'JSON',
        success: function(results){
            handLogResults(results);
        }
    });
}

function fetchLogListByLev(lev){
    var requestParameters = {'lev':currentLev};
    $.ajax({
        url: "getListByLev",
        type: "GET",
        dataType: 'JSON',
        data: requestParameters,
        success: function(results){
            handLogResults(results);
        }
    });
}

function fetchTestApi(){
    $.ajax({
        url: "http://gank.io/api/random/data/Android/20",
        type: "GET",
        dataType: 'JSON',
        success: function(jsn){
            // $("#db-data-div").empty();
            // $("#parent-data-div").empty();
            jsn.results.forEach(function(item){
                handItem({'time':item.publishedAt,'lev':(item.type == 'Android')?'DEBUG':'ERROR','tag':item.who,'msg':item.desc});
            })

            $('#log-table').DataTable({
                "pageLength": 250,
            });
        }
    });
}

function handLogResults(results){
    $("#db-data-div").empty();
    results.forEach(function(item){
        handItem(item);
    })

    $('#log-table').DataTable({
        "pageLength": 250,
    });

}

function handItem(item){
    var color = "#000000";
    if(item.lev == 'VERBOSE'){
    color = '#717171';
    }else if(item.lev == 'DEBUG'){
        color = '#2980b9';
    }else if(item.lev == 'INFO'){
        color = '#27ae60';
    }else if(item.lev == 'WARN'){
        color = '#f39c12';
    }else if(item.lev == 'ERROR'){
        color = '#c0392b';
    }
    
    item.msg = item.msg.replace("/n", "</br>")
    
    var line = '<tr style="color : '+color+'"><td>'+item.time+'</td><td>'+item.tag+'&nbsp;&nbsp;&nbsp;&nbsp'+item.msg+'</td></tr>';
    $("#db-data-div").append(line);
}