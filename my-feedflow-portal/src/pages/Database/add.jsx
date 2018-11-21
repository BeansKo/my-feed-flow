import React,{Component} from 'react';
import DatabaseForm from './components/Form';
import { withRouter } from 'react-router';
import {DatabaseService} from '../../service' 

@withRouter
export default class add extends Component{

    static displayName='addDatabase';
    
    constructor(props) {
        super(props);
        this.state = {};
    }

    onSubmit = async(data) => {
        await DatabaseService.insert(data);
        this.props.history.push('/database');
        return true;
    }

    render(){
        return(
            <div> 
                <DatabaseForm onSubmit={this.onSubmit}/>
            </div>
        );
    }

}