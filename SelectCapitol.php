<?php

    include_once dirname(__FILE__).'/Conn.php';
    $stmt=$con->prepare("SELECT idCapitol, numeCapitol FROM capitole");
    $stmt->execute();

    $stmt->bind_result($idCapitol,$numeCapitol);
    $capitol=array();

    while($stmt->fetch()){
        $temp=array();
        $temp['idCapitol']=$idCapitol;
        $temp['numeCapitol']=$numeCapitol;

        array_push($capitol,$temp);
    }

    echo json_encode($capitol);

?>


