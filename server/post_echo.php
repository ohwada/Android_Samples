<?php
// post_echo.php
// echoback post variables
// 2018-03-01 K.OHWADA
if($_SERVER["REQUEST_METHOD"] == "POST"){
        print_r($_POST);
} else {
    echo "plesse use POST method";
}
?>