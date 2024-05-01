<?php

include 'conn.php';

$senderID=$_POST['senderID'] ?? '';
$messageText=$_POST['messageText'] ?? '';
$time=$_POST['time'] ?? '';
$chatID=$_POST['chatID'] ?? '';

$newmessage=$_POST['newmessage'] ?? '';

$query = "UPDATE messages 
          SET messageText = '$newmessage' 
          WHERE messageText = '$messageText' 
          AND time = '$time'
          AND senderID = '$senderID'
          AND chatID = '$chatID'";

if(mysqli_query($conn,$query))
{
    echo "Record updated successfully";
}
else
{
    echo "Error deleting record: " . mysqli_error;
}

mysqli_close($conn);

?>