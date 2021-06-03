<?php

include_once dirname(__FILE__).'/Conn.php';
$stmt=$con->prepare("SELECT * FROM chestionare");
$stmt->execute();

$stmt->bind_result($idChestionar,$titluSubnivel,$informatie,$exemplu1,$exemplu2,$intrebare,$raspunsCorect,$raspunsuri,$indiciu,$idCapitol);
$chestionar=array();

while($stmt->fetch()){
    $temp=array();
    $temp['idChestionar']=$idChestionar;
    $temp['titluSubnivel']=$titluSubnivel;
    $temp['informatie']=$informatie;
    $temp['exemplu1']=$exemplu1;
    $temp['exemplu2']=$exemplu2;
    $temp['intrebare']=$intrebare;
    $temp['raspunsCorect']=$raspunsCorect;
    $temp['raspunsuri']=$raspunsuri;
    $temp['indiciu']=$indiciu;
    $temp['idCapitol']=$idCapitol;

    array_push($chestionar,$temp);
}

echo json_encode($chestionar);