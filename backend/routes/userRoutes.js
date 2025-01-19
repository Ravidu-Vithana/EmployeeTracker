import express from 'express';
import User from '../model/userModel.js';

const Router = express.Router();

Router.post('/auth', async (req, res) => {

    const userInDB = await User.findOne({email:req.body.email});
    
    if(userInDB == null){
        const user = new User(req.body);
        user.save()
        .then(result => {
            res.status(201).json({message:"registration success"});
            console.log("registration success");
        })
        .catch(err => {
            res.status(500).json({message: "error", error: err});
        });
    }else{
        res.status(200).json({message: "login success"});
        console.log("login success");
    }
});

export default Router;