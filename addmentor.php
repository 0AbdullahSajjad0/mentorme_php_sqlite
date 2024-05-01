<?php

include 'conn.php';

$response=array();

if(isset($_POST['image']))
{
    $u=$_POST['u'];
    $name=$_POST['name'] ?? '';
    $description=$_POST['description'] ?? '';
    $status=$_POST['status'] ?? '';
    $image=$_POST['image'];
    $imagename=$name.".jpeg";
    $path="mentorpics/".$imagename;
    file_put_contents($path,base64_decode($image));
    $response['url']="mentorme/".$path;
    $response['message']="Image Uploaded Successfully";
    $response['status']=1;
    $profpic="$u/mentorme/$path";

    // Update the profilecover in the users table
    $query="Insert into mentors(name, description, status, profilepic) 
        Values ('$name','$description','$status','$profpic')";
        
    if (mysqli_query($conn, $query)) {
        $response['db_message'] = "Record inserted successfully";
    } else {
        $response['db_message'] = "Error inserting record: " . mysqli_error($conn);
    }
    // if (mysqli_query($conn, $query)) {
    //     echo "Record updated successfully";
    // } else {
    //     echo "Error updating record: " . mysqli_error($conn);
    // }
}
else
{
    $response['message']="Incomplete Request";
    $response['status']=0;
}

echo json_encode($response);
?>