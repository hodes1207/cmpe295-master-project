/*EngineService server = (EngineService) request.getAttribute("server");
server.startService();
var progress = setInterval(function() {
    var $bar = $('.bar');
    
    if ($bar.width()==400 && serv.getInitProgress() == 1) {
        clearInterval(progress);
        $('.progress').removeClass('active');
    } else {
        $bar.width(serv.getInitProgress()*100);
    }
    $bar.text($bar.width()/4 + "%");
}, 800);
*/

var progress = setInterval(function() {
    var $bar = $('.bar');
    
    if ($bar.width()==400) {
        clearInterval(progress);
        $('.progress').removeClass('active');
    } else {
        $bar.width($bar.width()+20);
    }
    $bar.text($bar.width()/4 + "%");
}, 800);

