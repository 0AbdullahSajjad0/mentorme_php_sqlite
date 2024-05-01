<?php

include 'conn.php';

$userid=$_POST["UserId"] ?? '';

$query="SELECT * FROM reviews WHERE userID = $userid";
$result = mysqli_query($conn, $query);
$reponse=array();

if($result)

{

$reponse['reviews']=array();

while($row=mysqli_fetch_assoc($result))

{

$task=array();

$task['reviewID']= $row['reviewID'];

$task['mentorID']= $row['mentorID'];

$task['mentorName']= $row['mentorName'];

$task['userID']= $row['userID'];

$task['feedback']= $row['feedback'];

$task['rating']= $row['rating'];

array_push($reponse['reviews'],$task);

}

$reponse['success']=1;

}

else {

$reponse['success']=0;

}

print(json_encode($reponse));

mysqli_close($conn);

?>