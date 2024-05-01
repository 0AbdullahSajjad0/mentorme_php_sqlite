<?php

include 'conn.php';

$chatId=$_POST["chatId"] ?? '';

$query="SELECT * FROM messages WHERE chatID = $chatId";
$result = mysqli_query($conn, $query);
$reponse=array();

if($result)

{

$reponse['messages']=array();

while($row=mysqli_fetch_assoc($result))

{

$task=array();

$task['messageID']= $row['messageID'];

$task['senderID']= $row['senderID'];

$task['recieverID']= $row['recieverID'];

$task['messageText']= $row['messageText'];

$task['messageImage']= $row['messageImage'];

$task['messageFile']= $row['messageFile'];

$task['time']= $row['time'];

array_push($reponse['messages'],$task);

}

$reponse['success']=1;

}

else {

$reponse['success']=0;

}

print(json_encode($reponse));

mysqli_close($conn);

?>