<?php

include 'conn.php';

$senderID=$_POST['senderID'] ?? '';
$recieverID=$_POST['recieverID'] ?? '';
$messageText=$_POST['messageText'] ?? '';
$messageImage=$_POST['messageImage'] ?? '';
$messageFile=$_POST['messageFile'] ?? '';
$time=$_POST['time'] ?? '';
$chatID=$_POST['chatID'] ?? '';

$query="Insert into messages(senderID, recieverID, messageText, messageImage, messageFile, time, chatID) 
        Values ($senderID,$recieverID,'$messageText','$messageImage','$messageFile','$time',$chatID)";
        
if (mysqli_query($conn, $query)) {

    echo "Record inserted successfully";

} else {

    echo "Error deleting record: " . mysqli_error($conn);

}

 mysqli_close($conn);

 ?>