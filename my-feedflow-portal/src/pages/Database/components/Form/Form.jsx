import React, { Component } from 'react';
import IceContainer from '@icedesign/container';
import PropTypes from 'prop-types';
import { Input, Grid, Button, Select, Switch, Feedback } from '@icedesign/base';

export default class Form extends Component{
    static displayName = 'Form';

    constructor(props) {
        super(props);
        this.state = {
          loading : false,
          value: { },
        };
      }
}