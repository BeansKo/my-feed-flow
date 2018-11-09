import React, { Component } from 'react';
import { Button } from '@icedesign/base';
import { withRouter } from 'react-router';

@withRouter
export default class Database extends Component{
    static displayName='Database';

    constructor(props) {
        super(props);
        this.state = {};
      }

    render() {
    return (
        <div>
        <div style={{textAlign: 'right', paddingRight:'50px', width:'100%', backgroundColor:'#fff'}}>
            <Button type="primary" size="large" style={{minWidth:'100px'}} onClick={this.goAdd}>Add New Database</Button>
        </div>
        </div>
    );
    }
}