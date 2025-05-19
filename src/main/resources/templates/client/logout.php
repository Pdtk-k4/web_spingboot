<?php
include_once './lib/session.php'; 
Session::init();
Session::destroy();
header("Location: index.php");
exit();
?>
