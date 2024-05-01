<?php

include 'conn.php';

$query="SELECT * FROM users";
$result = mysqli_query($conn, $query);
$reponse=array();

if($result)

{

$reponse['users']=array();

while($row=mysqli_fetch_assoc($result))

{

$task=array();

$task['UserID']= $row['UserID'];

$task['name']= $row['name'];

$task['email']= $row['email'];

$task['profilepic']= $row['profilepic'];

array_push($reponse['users'],$task);

}

$reponse['success']=1;

}

else {

$reponse['success']=0;

}

print(json_encode($reponse));

mysqli_close($conn);

?>