import React, { Component } from 'react';
import JobTypeForm from './components/Form';
import { JobTypeService, JobStepService } from '../../service'
import { withRouter } from 'react-router';

@withRouter
export default class add extends Component {
  static displayName = 'addJobType';

  constructor(props) {
    super(props);
    this.state = {
      jobSteps : [],
    };
  }

  componentDidMount = async() => {
    let jobSteps = await JobStepService.list()
    this.setState({jobSteps})
  }

  onSubmit = async(data) =>{
    await JobTypeService.insert(data);
    this.props.history.push(`/jobType`)
    return true
  }

  render() {
    return (
      <div>
        <JobTypeForm jobSteps={this.state.jobSteps} onSubmit={this.onSubmit}/>
      </div>
    );
  }
}