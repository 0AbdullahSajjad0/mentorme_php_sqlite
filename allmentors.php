<?php

include 'conn.php';

$query="SELECT * FROM mentors";
$result = mysqli_query($conn, $query);
$reponse=array();

if($result)

{

$reponse['mentors']=array();

while($row=mysqli_fetch_assoc($result))

{

$task=array();

$task['mentorID']= $row['mentorID'];

$task['name']= $row['name'];

$task['description']= $row['description'];

$task['status']= $row['status'];

$task['profilepic']= $row['profilepic'];

array_push($reponse['mentors'],$task);

}

$reponse['success']=1;

}

else {

$reponse['success']=0;

}

print(json_encode($reponse));

mysqli_close($conn);

?>