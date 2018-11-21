import React, { Component } from 'react';
import EmailForm from './components/Form';
import { EmailService } from '../../service';
import { withRouter } from 'react-router';

@withRouter
export default class add extends Component {
    static displayName = 'addEmail';
    constructor(props) {
        super(props);
        this.state = {};
    }

    onSubmit = async(data) =>{
        await EmailService.insert(data);
        this.props.history.push(`/email`)
        return true
    }

    render() {
        return (
          <div>
            <EmailForm onSubmit={this.onSubmit}/>
          </div>
        );
    }
}