import React, { Component } from 'react';
import { Button } from '@icedesign/base';
import { withRouter } from 'react-router';
import EmailTable from './components/List';
import { EmailService } from '../../service';

export default class Email extends Component {
    static displayName = 'Email';

    constructor(props) {
        super(props);
        this.state = {};
    }

    list = async() =>{
        return await EmailService.list()
    }
    
    edit = (id) =>{
        this.props.history.push(`/email/edit/${id}`)
    }
    
    del = async(id) =>{
        await EmailService.del(id)
    }

    goAdd = () =>{
        this.props.history.push(`/email/add`)
    }

    render() {
        return (
            <div>
                <div style={{textAlign: 'right', paddingTop:'10px', paddingRight:'50px', width:'100%', backgroundColor:'#fff'}}>
                    <Button type="primary" size="large" style={{minWidth:'100px'}} onClick={this.goAdd}>Add New Email</Button>
                </div>
                <EmailTable list={this.list} edit={this.edit} del={this.del}/>
          </div>
        );
    }
}