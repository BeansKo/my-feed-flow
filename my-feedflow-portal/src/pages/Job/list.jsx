import React, { Component } from 'react';
import JobTable from './components/List';
import { withRouter } from 'react-router';
import { JobService } from '../../service';

@withRouter
export default class Job extends Component {
  static displayName = 'Job';

  constructor(props) {
    super(props);
    this.state = {};
  }

  list = async() =>{
    return await JobService.list()
  }

  edit = (id) =>{
    this.props.history.push(`/job/edit/${id}`)
  }

  run = async(id) =>{
    return await JobService.run(id)
  }

  del = async(id) =>{
    await JobService.del(id)
  }

  goAdd = () =>{
    this.props.history.push(`/job/add`)
  }

  render() {
    return (
      <div>
        <JobTable list={this.list} run={this.run} edit={this.edit} del={this.del} goAdd={this.goAdd} />
      </div>
    );
  }
}