import React, { Component } from 'react';
import DatabaseForm from './components/Form';
import { withRouter } from 'react-router';
import { DatabaseService } from '../../service'

@withRouter
export default class edit extends Component{
    static displayName= 'edit';

    constructor(props) {
        super(props);
        this.state = { };
    }

    loadData = async() =>{
        return await DatabaseService.get(this.props.match.params.id)
      }
    
    onSubmit = async(data) =>{
        await DatabaseService.update(data);
        this.props.history.push(`/database`)
        return true
    }
    
    render() {
        return (
            <div>
                <DatabaseForm id={this.props.match.params.id} loadData={this.loadData} onSubmit={this.onSubmit}/>
            </div>
        );
    }
}