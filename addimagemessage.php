<?php

include 'conn.php';

$response=array();

if(isset($_POST['messageImage']) && $_POST['messageImage'] != NULL && $_POST['messageImage'] != "NULL")
{
    $u=$_POST['u'];
    $senderID=$_POST['senderID'] ?? '';
    $recieverID=$_POST['recieverID'] ?? '';
    $messageText=$_POST['messageText'] ?? '';
    $messageImage=$_POST['messageImage'] ?? '';
    $messageFile=$_POST['messageFile'] ?? '';
    $time=$_POST['time'] ?? '';
    $filetime = str_replace(":", "_", $_POST['time'] ?? '');
    $chatID=$_POST['chatID'] ?? '';


    $imagename=$senderID.$filetime.".jpeg";
    $path="messagepics/".$imagename;
    file_put_contents($path,base64_decode($messageImage));
    $response['url']="mentorme/".$path;
    $response['message']="Image Uploaded Successfully";
    $response['status']=1;

    $query="Insert into messages(senderID, recieverID, messageText, messageImage, messageFile, time, chatID) 
            Values ($senderID,$recieverID,'$messageText','$u/mentorme/$path','$messageFile','$time',$chatID)";
            
    mysqli_query($conn, $query);
}
else
{
    $response['message']="Incomplete Request";
    $response['status']=0;
}

echo json_encode($response);

 ?>