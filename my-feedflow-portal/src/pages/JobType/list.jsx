import React, { Component } from 'react';
import { Button } from '@icedesign/base';
import JobTypeTable from './components/List';
import { withRouter } from 'react-router';
import { JobTypeService } from '../../service';

@withRouter
export default class JobType extends Component {
    static displayName = 'JobType';

    constructor(props) {
        super(props);
        this.state = {};
    }

    list = async() =>{
        return await JobTypeService.list()
      }
    
    edit = (id) =>{
        this.props.history.push(`/jobType/edit/${id}`)
    }
    
    del = async(id) =>{
        await JobTypeService.del(id)
    }

    goAdd = () =>{
        this.props.history.push(`/jobType/add`)
    }

    render() {
        return (
            <div>
                <div style={{textAlign: 'right', paddingTop:'20px', paddingRight:'50px', width:'100%', backgroundColor:'#fff'}}>
                    <Button type="primary" size="large" style={{minWidth:'100px'}} onClick={this.goAdd}>Add New JobType</Button>
                </div>
                <JobTypeTable list={this.list} edit={this.edit} del={this.del}/>
          </div>
        );
    }
}