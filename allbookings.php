<?php

include 'conn.php';

$userid=$_POST["UserId"] ?? '';

$query="SELECT * FROM bookings WHERE userID = $userid";
$result = mysqli_query($conn, $query);
$reponse=array();

if($result)

{

$reponse['bookings']=array();

while($row=mysqli_fetch_assoc($result))

{

$task=array();

$task['bookingID']= $row['bookingID'];

$task['mentorID']= $row['mentorID'];

$task['userID']= $row['userID'];

$task['date']= $row['date'];

$task['time']= $row['time'];

array_push($reponse['bookings'],$task);

}

$reponse['success']=1;

}

else {

$reponse['success']=0;

}

print(json_encode($reponse));

mysqli_close($conn);

?>