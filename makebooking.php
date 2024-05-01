<?php

include 'conn.php';

$mentorID=$_POST['mentorID'] ?? '';
$userID=$_POST['userID'] ?? '';
$date=$_POST['date'] ?? '';
$time=$_POST['time'] ?? '';

$query="Insert into bookings(mentorID, userID, date, time) 
        Values ('$mentorID','$userID','$date','$time')";
        
if (mysqli_query($conn, $query)) {

    echo "Record inserted successfully";

} else {

    echo "Error deleting record: " . mysqli_error($conn);

}

 mysqli_close($conn);

 ?>