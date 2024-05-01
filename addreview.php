<?php

include 'conn.php';

$mentorID=$_POST['mentorID'] ?? '';
$mentorName=$_POST['mentorname'] ?? '';
$userID=$_POST['userID'] ?? '';
$feedback=$_POST['feedback'] ?? '';
$rating=$_POST['rating'] ?? '';

$query="Insert into reviews (mentorID, mentorName, userID, feedback, rating) 
        Values ('$mentorID','$mentorName','$userID','$feedback', $rating)";
        
if (mysqli_query($conn, $query)) {

    echo "Record inserted successfully";

} else {

    echo "Error deleting record: " . mysqli_error($conn);

}

 mysqli_close($conn);

 ?>