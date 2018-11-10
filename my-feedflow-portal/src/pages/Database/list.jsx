import React, { Component } from 'react';
import { Button } from '@icedesign/base';
import { withRouter } from 'react-router';
import DataBaseTable from './components/List';
import {DatabaseService} from '../../service';

@withRouter
export default class Database extends Component{
    static displayName='Database';

    constructor(props) {
        super(props);
        this.state = {};
      }

    list = async() =>{
        return await DatabaseService.list()
    }

    goAdd = () =>{
        this.props.history.push('/database/add')
    }
    
    render() {
        return (
            <div>
                <div style={{textAlign: 'right', paddingTop:'10px', paddingRight:'50px', width:'100%', backgroundColor:'#fff'}}>
                    <Button type="primary" size="large" style={{minWidth:'100px'}} onClick={this.goAdd}>Add New Database</Button>
                </div>
                <DataBaseTable list={this.list}/>
            </div>
        );
    }
}