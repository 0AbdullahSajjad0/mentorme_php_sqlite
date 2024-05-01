<?php

include 'conn.php';

$time=$_POST['time'] ?? '';

if(isset($_POST['messageImage']))
{
    $messageImage = $_POST['messageImage'];
    $query="DELETE FROM messages WHERE messageImage = '$messageImage' and time = '$time' ";

    if(mysqli_query($conn,$query))
    {
        echo "Record deleted successfully";
    }
    else
    {
        echo "Error deleting record: " . mysqli_error;
    }
}
else if(isset($_POST['messageText']))
{
    $messageText = $_POST['messageText'];
    $query="DELETE FROM messages WHERE messageText = '$messageText' and time = '$time' ";

    if(mysqli_query($conn,$query))
    {
        echo "Record deleted successfully";
    }
    else
    {
        echo "Error deleting record: " . mysqli_error;
    }
}

mysqli_close($conn);

?>